package com.assessment.employee.config;

import com.assessment.employee.entity.Employee;
import com.assessment.employee.entity.EmployeeStatus;
import com.assessment.employee.entity.User;
import com.assessment.employee.repository.EmployeeRepository;
import com.assessment.employee.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ROLE_ADMIN")
                    .active(true)
                    .build());
            log.info("Seeded default user: admin / admin123");
        }

        if (employeeRepository.count() == 0) {
            LocalDate today = LocalDate.now();
            YearMonth current = YearMonth.from(today);
            List<Employee> seed = List.of(
                    employee("EMP001", "Ava", "Patel", "ava.patel@example.com", "Engineering", EmployeeStatus.ACTIVE, current.atDay(1)),
                    employee("EMP002", "Noah", "Kim", "noah.kim@example.com", "Engineering", EmployeeStatus.ACTIVE, current.atDay(Math.min(5, current.lengthOfMonth()))),
                    employee("EMP003", "Mia", "Garcia", "mia.garcia@example.com", "HR", EmployeeStatus.ON_LEAVE, current.atDay(Math.min(10, current.lengthOfMonth()))),
                    employee("EMP004", "Liam", "Chen", "liam.chen@example.com", "Finance", EmployeeStatus.ACTIVE, today),
                    employee("EMP005", "Sofia", "Nguyen", "sofia.nguyen@example.com", "Sales", EmployeeStatus.INACTIVE, today.minusMonths(2)),
                    employee("EMP006", "Ethan", "Brooks", "ethan.brooks@example.com", "Sales", EmployeeStatus.ACTIVE, today.minusMonths(1)),
                    employee("EMP007", "Olivia", "Singh", "olivia.singh@example.com", "Marketing", EmployeeStatus.ACTIVE, today.minusMonths(3)),
                    employee("EMP008", "Lucas", "Martinez", "lucas.martinez@example.com", "Engineering", EmployeeStatus.ACTIVE, today.minusMonths(4)),
                    employee("EMP009", "Emma", "Wilson", "emma.wilson@example.com", "HR", EmployeeStatus.ACTIVE, today.minusMonths(6)),
                    employee("EMP010", "James", "Taylor", "james.taylor@example.com", "Finance", EmployeeStatus.ON_LEAVE, today.minusMonths(8)),
                    employee("EMP011", "Amelia", "Davis", "amelia.davis@example.com", "Marketing", EmployeeStatus.ACTIVE, today.minusYears(1)),
                    employee("EMP012", "Benjamin", "Lee", "benjamin.lee@example.com", "Engineering", EmployeeStatus.INACTIVE, today.minusYears(2))
            );
            employeeRepository.saveAll(seed);
            log.info("Seeded {} sample employees", seed.size());
            return;
        }

        // Existing DB from earlier seed: move a few hire dates into the current month for report demos
        YearMonth current = YearMonth.now();
        List<Employee> thisMonth = employeeRepository.findAllForReport(
                null, null, current.atDay(1), current.atEndOfMonth());
        if (thisMonth.isEmpty()) {
            employeeRepository.findAll().stream().limit(4).forEach(emp -> {
                emp.setHireDate(current.atDay(Math.min((int) (emp.getId() == null ? 1 : emp.getId()), current.lengthOfMonth())));
                emp.setDeleted(false);
                employeeRepository.save(emp);
            });
            log.info("Adjusted sample hire dates into current month for report demos");
        }
    }

    private Employee employee(
            String code,
            String first,
            String last,
            String email,
            String department,
            EmployeeStatus status,
            LocalDate hireDate) {
        return Employee.builder()
                .employeeCode(code)
                .firstName(first)
                .lastName(last)
                .email(email)
                .phone("+1-555-0100")
                .department(department)
                .status(status)
                .hireDate(hireDate)
                .deleted(false)
                .build();
    }
}
