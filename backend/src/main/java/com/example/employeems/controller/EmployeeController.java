package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.dto.EmployeeCreateRequest;
import com.example.employeems.dto.EmployeeResponse;
import com.example.employeems.dto.EmployeeUpdateRequest;
import com.example.employeems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * REST controller for Employee CRUD APIs.
 *
 * Controller responsibility:
 * - receive HTTP requests from the frontend
 * - validate request bodies with @Valid
 * - call EmployeeService for business logic
 * - return JSON responses in a consistent ApiResponse format
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> list(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size
    ) {
        Page<EmployeeResponse> p = employeeService.list(search, departmentId, page, size, "id", "desc");

        HashMap<String, Object> data = new HashMap<>();
        data.put("items", p.getContent());
        data.put("page", p.getNumber());
        data.put("size", p.getSize());
        data.put("totalElements", p.getTotalElements());
        data.put("totalPages", p.getTotalPages());

        return ApiResponse.ok("Employees loaded", data);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<EmployeeResponse> getById(@PathVariable("id") Long id) {
        return ApiResponse.ok("Employee loaded", employeeService.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<EmployeeResponse> create(@Valid @RequestBody EmployeeCreateRequest request) {
        return ApiResponse.ok("Employee created", employeeService.create(request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<EmployeeResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody EmployeeUpdateRequest request
    ) {
        return ApiResponse.ok("Employee updated", employeeService.update(id, request));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> delete(@PathVariable("id") Long id) {
        employeeService.deleteById(id);
        return ApiResponse.ok("Employee deleted", null);
    }
}
