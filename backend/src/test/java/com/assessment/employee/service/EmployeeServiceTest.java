package com.assessment.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.assessment.employee.dto.EmployeeRequest;
import com.assessment.employee.dto.EmployeeResponse;
import com.assessment.employee.entity.Employee;
import com.assessment.employee.entity.EmployeeStatus;
import com.assessment.employee.exception.ApiException;
import com.assessment.employee.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void create_persistsNewEmployee() {
        EmployeeRequest request = new EmployeeRequest(
                "EMP100",
                "Ada",
                "Lovelace",
                "ada@example.com",
                "555-0100",
                "Engineering",
                EmployeeStatus.ACTIVE,
                LocalDate.of(2024, 1, 10));

        when(employeeRepository.existsByEmployeeCodeAndDeletedFalse("EMP100")).thenReturn(false);
        when(employeeRepository.existsByEmailAndDeletedFalse("ada@example.com")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        EmployeeResponse response = employeeService.create(request);

        assertEquals(42L, response.id());
        assertEquals("EMP100", response.employeeCode());
        assertEquals("ada@example.com", response.email());

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        assertEquals(false, captor.getValue().isDeleted());
    }

    @Test
    void create_WhenEmailExists_ThrowsConflict() {
        EmployeeRequest request = new EmployeeRequest(
                "EMP100",
                "Ada",
                "Lovelace",
                "ada@example.com",
                null,
                "Engineering",
                EmployeeStatus.ACTIVE,
                LocalDate.of(2024, 1, 10));

        when(employeeRepository.existsByEmployeeCodeAndDeletedFalse("EMP100")).thenReturn(false);
        when(employeeRepository.existsByEmailAndDeletedFalse("ada@example.com")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> employeeService.create(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void softDelete_MarksEmployeeDeleted() {
        Employee existing = Employee.builder()
                .id(7L)
                .employeeCode("EMP007")
                .firstName("Olivia")
                .lastName("Singh")
                .email("olivia@example.com")
                .department("Marketing")
                .status(EmployeeStatus.ACTIVE)
                .hireDate(LocalDate.of(2022, 9, 30))
                .deleted(false)
                .build();

        when(employeeRepository.findByIdAndDeletedFalse(7L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        employeeService.softDelete(7L);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        assertEquals(true, captor.getValue().isDeleted());
    }
}
