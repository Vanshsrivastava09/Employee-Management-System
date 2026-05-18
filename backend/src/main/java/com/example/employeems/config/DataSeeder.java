package com.example.employeems.config;

import com.example.employeems.entity.AdminUser;
import com.example.employeems.entity.Department;
import com.example.employeems.entity.Employee;
import com.example.employeems.repository.AdminUserRepository;
import com.example.employeems.repository.DepartmentRepository;
import com.example.employeems.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

/**
 * Inserts beginner-friendly sample data when the database is empty.
 *
 * This helps the project run immediately in VS Code after MySQL is connected:
 * - default admin: admin / admin123
 * - three departments
 * - three sample employees
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            AdminUserRepository adminUserRepository,
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedAdmin(adminUserRepository, passwordEncoder);

            Department engineering = seedDepartment(departmentRepository, "Engineering");
            Department hr = seedDepartment(departmentRepository, "Human Resources");
            Department marketing = seedDepartment(departmentRepository, "Marketing");

            seedEmployee(employeeRepository, "Aarav Kumar", "aarav.kumar@example.com", "9876543210", engineering, true);
            seedEmployee(employeeRepository, "Meera Sharma", "meera.sharma@example.com", "9123456780", hr, true);
            seedEmployee(employeeRepository, "Rohan Verma", "rohan.verma@example.com", "9988776655", marketing, false);
        };
    }

    private void seedAdmin(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        if (adminUserRepository.findByUsername("admin").isPresent()) {
            return;
        }

        AdminUser admin = new AdminUser();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        adminUserRepository.save(admin);
    }

    private Department seedDepartment(DepartmentRepository departmentRepository, String name) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> departmentRepository.save(new Department(name)));
    }

    private void seedEmployee(
            EmployeeRepository employeeRepository,
            String fullName,
            String email,
            String phone,
            Department department,
            boolean active
    ) {
        if (employeeRepository.findByEmail(email).isPresent()) {
            return;
        }

        Instant now = Instant.now();

        Employee employee = new Employee();
        employee.setFullName(fullName);
        employee.setEmail(email);
        employee.setPhone(phone);
        employee.setDepartment(department);
        employee.setActive(active);
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        employeeRepository.save(employee);
    }
}
