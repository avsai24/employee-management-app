package com.assessment.employee.dto;

import com.assessment.employee.entity.Employee;
import com.assessment.employee.entity.EmployeeStatus;
import java.time.Instant;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String phone,
        String department,
        EmployeeStatus status,
        LocalDate hireDate,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getStatus(),
                employee.getHireDate(),
                employee.getCreatedAt(),
                employee.getUpdatedAt());
    }
}
