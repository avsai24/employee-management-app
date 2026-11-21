package com.assessment.employee.config;

import com.assessment.employee.entity.Employee;
import com.assessment.employee.entity.EmployeeStatus;
import com.assessment.employee.entity.User;
import com.assessment.employee.repository.EmployeeRepository;
import com.assessment.employee.repository.UserRepository;
import java.time.LocalDate;
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
            List<Employee> seed = List.of(
                    employee("EMP001", "Ava", "Patel", "ava.patel@example.com", "Engineering", EmployeeStatus.ACTIVE, LocalDate.of(2022, 3, 15)),
                    employee("EMP002", "Noah", "Kim", "noah.kim@example.com", "Engineering", EmployeeStatus.ACTIVE, LocalDate.of(2021, 7, 1)),
                    employee("EMP003", "Mia", "Garcia", "mia.garcia@example.com", "HR", EmployeeStatus.ON_LEAVE, LocalDate.of(2020, 11, 20)),
                    employee("EMP004", "Liam", "Chen", "liam.chen@example.com", "Finance", EmployeeStatus.ACTIVE, LocalDate.of(2023, 1, 9)),
                    employee("EMP005", "Sofia", "Nguyen", "sofia.nguyen@example.com", "Sales", EmployeeStatus.INACTIVE, LocalDate.of(2019, 5, 4)),
                    employee("EMP006", "Ethan", "Brooks", "ethan.brooks@example.com", "Sales", EmployeeStatus.ACTIVE, LocalDate.of(2024, 2, 12)),
                    employee("EMP007", "Olivia", "Singh", "olivia.singh@example.com", "Marketing", EmployeeStatus.ACTIVE, LocalDate.of(2022, 9, 30)),
                    employee("EMP008", "Lucas", "Martinez", "lucas.martinez@example.com", "Engineering", EmployeeStatus.ACTIVE, LocalDate.of(2023, 6, 18)),
                    employee("EMP009", "Emma", "Wilson", "emma.wilson@example.com", "HR", EmployeeStatus.ACTIVE, LocalDate.of(2021, 4, 22)),
                    employee("EMP010", "James", "Taylor", "james.taylor@example.com", "Finance", EmployeeStatus.ON_LEAVE, LocalDate.of(2020, 8, 8)),
                    employee("EMP011", "Amelia", "Davis", "amelia.davis@example.com", "Marketing", EmployeeStatus.ACTIVE, LocalDate.of(2024, 5, 1)),
                    employee("EMP012", "Benjamin", "Lee", "benjamin.lee@example.com", "Engineering", EmployeeStatus.INACTIVE, LocalDate.of(2018, 12, 3))
            );
            employeeRepository.saveAll(seed);
            log.info("Seeded {} sample employees", seed.size());
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
