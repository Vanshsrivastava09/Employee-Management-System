package com.example.employeems.service;

import com.example.employeems.entity.Department;
import com.example.employeems.exception.BadRequestException;
import com.example.employeems.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for department business rules.
 *
 * Controllers receive HTTP requests, but services decide what is valid and
 * when repository/database methods should be called.
 */
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Department> list() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department create(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Department name is required");
        }

        String cleanName = name.trim();
        if (departmentRepository.findByName(cleanName).isPresent()) {
            throw new BadRequestException("Department already exists");
        }

        return departmentRepository.save(new Department(cleanName));
    }
}
