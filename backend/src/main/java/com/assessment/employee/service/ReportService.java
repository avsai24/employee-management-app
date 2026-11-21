package com.assessment.employee.service;

import com.assessment.employee.dto.EmployeeResponse;
import com.assessment.employee.dto.ReportFilter;
import com.assessment.employee.dto.ReportSummaryResponse;
import com.assessment.employee.entity.Employee;
import com.assessment.employee.entity.EmployeeStatus;
import com.assessment.employee.exception.ApiException;
import com.assessment.employee.repository.EmployeeRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getReport(ReportFilter filter, Pageable pageable) {
        ReportFilter normalized = normalize(filter);
        return employeeRepository
                .findForReport(
                        normalized.status(),
                        normalized.department(),
                        normalized.fromDate(),
                        normalized.toDate(),
                        pageable)
                .map(EmployeeResponse::from);
    }

    @Transactional(readOnly = true)
    public ReportSummaryResponse getSummary(ReportFilter filter) {
        ReportFilter normalized = normalize(filter);

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (EmployeeStatus status : EmployeeStatus.values()) {
            byStatus.put(status.name(), 0L);
        }
        for (Object[] row : employeeRepository.countGroupedByStatus(
                normalized.status(), normalized.department(), normalized.fromDate(), normalized.toDate())) {
            byStatus.put(((EmployeeStatus) row[0]).name(), (Long) row[1]);
        }

        Map<String, Long> byDepartment = new LinkedHashMap<>();
        long total = 0L;
        for (Object[] row : employeeRepository.countGroupedByDepartment(
                normalized.status(), normalized.department(), normalized.fromDate(), normalized.toDate())) {
            long count = (Long) row[1];
            byDepartment.put((String) row[0], count);
            total += count;
        }

        return new ReportSummaryResponse(total, byStatus, byDepartment);
    }

    @Transactional(readOnly = true)
    public List<String> getDepartments() {
        return employeeRepository.findDistinctDepartments();
    }

    @Transactional(readOnly = true)
    public byte[] exportExcel(ReportFilter filter) {
        List<Employee> employees = loadAll(filter);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Employee Report");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                    "Code", "First Name", "Last Name", "Email", "Phone", "Department", "Status", "Hire Date"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Employee employee : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(employee.getEmployeeCode());
                row.createCell(1).setCellValue(employee.getFirstName());
                row.createCell(2).setCellValue(employee.getLastName());
                row.createCell(3).setCellValue(employee.getEmail());
                row.createCell(4).setCellValue(employee.getPhone() == null ? "" : employee.getPhone());
                row.createCell(5).setCellValue(employee.getDepartment());
                row.createCell(6).setCellValue(employee.getStatus().name());
                row.createCell(7).setCellValue(employee.getHireDate().format(DATE_FMT));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new ApiException("Failed to generate Excel export", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportPdf(ReportFilter filter) {
        List<Employee> employees = loadAll(filter);
        ReportFilter normalized = normalize(filter);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            document.add(new Paragraph("Employee Management Report", titleFont));
            document.add(new Paragraph("Generated: " + LocalDate.now().format(DATE_FMT), metaFont));
            document.add(new Paragraph(
                    "Filters: status=" + valueOrAll(normalized.status())
                            + ", department=" + valueOrAll(normalized.department())
                            + ", from=" + normalized.fromDate()
                            + ", to=" + normalized.toDate(),
                    metaFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[] {1.2f, 1.4f, 1.4f, 2.4f, 1.5f, 1.5f, 1.2f, 1.2f});

            String[] headers = {
                    "Code", "First Name", "Last Name", "Email", "Phone", "Department", "Status", "Hire Date"
            };
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Employee employee : employees) {
                table.addCell(new Phrase(employee.getEmployeeCode(), bodyFont));
                table.addCell(new Phrase(employee.getFirstName(), bodyFont));
                table.addCell(new Phrase(employee.getLastName(), bodyFont));
                table.addCell(new Phrase(employee.getEmail(), bodyFont));
                table.addCell(new Phrase(employee.getPhone() == null ? "" : employee.getPhone(), bodyFont));
                table.addCell(new Phrase(employee.getDepartment(), bodyFont));
                table.addCell(new Phrase(employee.getStatus().name(), bodyFont));
                table.addCell(new Phrase(employee.getHireDate().format(DATE_FMT), bodyFont));
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total records: " + employees.size(), metaFont));
            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new ApiException("Failed to generate PDF export", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<Employee> loadAll(ReportFilter filter) {
        ReportFilter normalized = normalize(filter);
        return employeeRepository.findAllForReport(
                normalized.status(),
                normalized.department(),
                normalized.fromDate(),
                normalized.toDate());
    }

    private ReportFilter normalize(ReportFilter filter) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate from = filter.fromDate() != null ? filter.fromDate() : currentMonth.atDay(1);
        LocalDate to = filter.toDate() != null ? filter.toDate() : currentMonth.atEndOfMonth();

        if (from.isAfter(to)) {
            throw new ApiException("fromDate must be less than or equal to toDate", HttpStatus.BAD_REQUEST);
        }

        String department = filter.department() == null || filter.department().isBlank()
                ? null
                : filter.department().trim();

        return new ReportFilter(filter.status(), department, from, to);
    }

    private String valueOrAll(Object value) {
        return value == null || String.valueOf(value).isBlank() ? "ALL" : String.valueOf(value);
    }
}
