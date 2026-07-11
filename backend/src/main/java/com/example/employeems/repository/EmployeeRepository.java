package com.example.employeems.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employeems.entity.Employee;

/**
 * Repository layer for employee database operations.
 *
 * Spring Data JPA automatically creates the implementation at runtime.
 * Extending JpaRepository gives ready-made CRUD methods such as save,
 * findById, findAll, and deleteById.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByResetToken(String resetToken);

    /**
     * Search employees by keyword in fullName/email/phone.
     */
    Page<Employee> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
            String fullNameKeyword,
            String emailKeyword,
            String phoneKeyword,
            Pageable pageable
    );

    /**
     * Filter by department id.
     */
    Page<Employee> findByDepartment_Id(Long departmentId, Pageable pageable);

    /**
     * Filter by department id + keyword (using combined approach).
     * We'll reuse the base keyword search and filter in service for beginner-friendliness.
     */
}
