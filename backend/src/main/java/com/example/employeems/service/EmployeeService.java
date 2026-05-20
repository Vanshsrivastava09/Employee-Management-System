package com.example.employeems.service;

import com.example.employeems.dto.EmployeeCreateRequest;
import com.example.employeems.dto.EmployeeResponse;
import com.example.employeems.dto.EmployeeUpdateRequest;
import com.example.employeems.exception.BadRequestException;
import com.example.employeems.exception.NotFoundException;
import com.example.employeems.entity.Department;
import com.example.employeems.entity.Employee;
import com.example.employeems.repository.DepartmentRepository;
import com.example.employeems.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service layer for employee business logic.
 *
 * Service responsibility:
 * - validate business rules
 * - load related data like Department
 * - call repositories for database work
 * - convert Entity objects to DTO responses for the frontend
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> list(String search, Long departmentId, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        List<Employee> all = employeeRepository.findAll();
        List<Employee> filtered = new ArrayList<>();

        boolean hasSearch = false;
        String s = null;

        if (search != null) {
            if (!search.isBlank()) {
                hasSearch = true;
                s = search.trim().toLowerCase();
            }
        }

        // Filter by department and search (in-memory)
        for (Employee e : all) {
            if (departmentId != null) {
                if (e.getDepartment() == null || !departmentId.equals(e.getDepartment().getId())) {
                    continue;
                }
            }

            if (hasSearch) {
                String fullName = e.getFullName() == null ? "" : e.getFullName().toLowerCase();
                String email = e.getEmail() == null ? "" : e.getEmail().toLowerCase();
                String phone = e.getPhone() == null ? "" : e.getPhone().toLowerCase();

                boolean matches = false;
                if (fullName.contains(s)) matches = true;
                if (email.contains(s)) matches = true;
                if (phone.contains(s)) matches = true;

                if (!matches) {
                    continue;
                }
            }

            filtered.add(e);
        }

        // Sort (only supports id for this beginner project)
        if ("desc".equalsIgnoreCase(sortDir)) {
            filtered.sort(Comparator.comparing(Employee::getId).reversed());
        } else {
            filtered.sort(Comparator.comparing(Employee::getId));
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<EmployeeResponse> items = new ArrayList<>();
        if (start < filtered.size()) {
            for (Employee e : filtered.subList(start, end)) {
                items.add(toResponse(e));
            }
        }

        return new PageImpl<>(items, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        if (id == null) throw new BadRequestException("Employee id is required");

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id=" + id));

        return toResponse(emp);
    }

    @Transactional
    public EmployeeResponse create(EmployeeCreateRequest request) {
        validateCreateRequest(request);

        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found with id=" + request.getDepartmentId()));

        Employee emp = new Employee();
        emp.setFullName(request.getFullName().trim());
        emp.setEmail(request.getEmail().trim().toLowerCase());
        emp.setPhone(trimToNull(request.getPhone()));
        emp.setDepartment(dept);
        emp.setActive(request.isActive());
        emp.setPhotoUrl(request.getPhotoUrl());

        Instant now = Instant.now();
        emp.setCreatedAt(now);
        emp.setUpdatedAt(now);

        return toResponse(employeeRepository.save(emp));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        if (id == null) throw new BadRequestException("Employee id is required");
        if (request == null) throw new BadRequestException("Request body is required");

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id=" + id));

        if (request.getFullName() != null) emp.setFullName(request.getFullName().trim());
        if (request.getEmail() != null) emp.setEmail(request.getEmail().trim().toLowerCase());
        if (request.getPhone() != null) emp.setPhone(trimToNull(request.getPhone()));

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found with id=" + request.getDepartmentId()));
            emp.setDepartment(dept);
        }

        emp.setActive(request.isActive());

        if (request.getPhotoUrl() != null) {
            emp.setPhotoUrl(request.getPhotoUrl());
        }

        emp.setUpdatedAt(Instant.now());
        return toResponse(employeeRepository.save(emp));
    }

    @Transactional
    public void deleteById(Long id) {
        if (id == null) throw new BadRequestException("Employee id is required");

        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException("Employee not found with id=" + id);
        }

        employeeRepository.deleteById(id);
    }

    private void validateCreateRequest(EmployeeCreateRequest request) {
        if (request == null) throw new BadRequestException("Request body is required");

        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new BadRequestException("fullName is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("email is required");
        }
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new BadRequestException("phone is required");
        }
        if (request.getDepartmentId() == null) {
            throw new BadRequestException("departmentId is required");
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private EmployeeResponse toResponse(Employee emp) {
        EmployeeResponse res = new EmployeeResponse();
        res.setId(emp.getId());
        res.setFullName(emp.getFullName());
        res.setEmail(emp.getEmail());
        res.setPhone(emp.getPhone());

        if (emp.getDepartment() != null) {
            res.setDepartmentId(emp.getDepartment().getId());
            res.setDepartmentName(emp.getDepartment().getName());
        }

        res.setActive(emp.isActive());
        res.setPhotoUrl(emp.getPhotoUrl());
        res.setCreatedAt(emp.getCreatedAt());
        res.setUpdatedAt(emp.getUpdatedAt());
        return res;
    }
}
