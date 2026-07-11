package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.entity.Salary;
import com.example.employeems.dto.SalaryResponse;
import com.example.employeems.dto.SalaryCreateRequest;
import com.example.employeems.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) { this.salaryService = salaryService; }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> list() {
        List<Salary> items = salaryService.listAll();
        List<SalaryResponse> dtos = items.stream().map(this::toDto).toList();
        HashMap<String,Object> data = new HashMap<>();
        data.put("items", dtos);
        return ApiResponse.ok("Salaries loaded", data);
    }

    private SalaryResponse toDto(Salary s) {
        if (s == null) return null;
        SalaryResponse r = new SalaryResponse();
        r.setId(s.getId());
        if (s.getEmployee() != null) {
            r.setEmployeeId(s.getEmployee().getId());
            r.setEmployeeName(s.getEmployee().getFullName());
        }
        r.setAmount(s.getAmount());
        r.setPeriod(s.getPeriod());
        r.setStatus(s.getStatus());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        return r;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<SalaryResponse> getById(@PathVariable Long id) { return ApiResponse.ok("Salary loaded", toDto(salaryService.getById(id))); }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<SalaryResponse> create(@Valid @RequestBody SalaryCreateRequest request) {
        Salary s = new Salary();
        s.setAmount(request.getAmount());
        s.setPeriod(request.getPeriod());
        s.setStatus(request.getStatus() == null ? "PENDING" : request.getStatus());

        Salary created = salaryService.create(request.getEmployeeId(), s);
        return ApiResponse.ok("Salary created", toDto(created));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<SalaryResponse> update(@PathVariable Long id, @RequestBody Salary payload) { return ApiResponse.ok("Salary updated", toDto(salaryService.update(id, payload))); }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> delete(@PathVariable Long id) { salaryService.delete(id); return ApiResponse.ok("Salary deleted", null); }
}
