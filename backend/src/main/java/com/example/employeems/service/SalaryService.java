package com.example.employeems.service;

import com.example.employeems.entity.Employee;
import com.example.employeems.entity.Salary;
import com.example.employeems.exception.NotFoundException;
import com.example.employeems.repository.EmployeeRepository;
import com.example.employeems.repository.SalaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    public SalaryService(SalaryRepository salaryRepository, EmployeeRepository employeeRepository) {
        this.salaryRepository = salaryRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Salary> listAll() {
        return salaryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Salary getById(Long id) {
        return salaryRepository.findById(id).orElseThrow(() -> new NotFoundException("Salary not found"));
    }

    @Transactional
    public Salary create(Long employeeId, Salary payload) {
        Employee emp = employeeRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Employee not found"));
        payload.setEmployee(emp);
        Instant now = Instant.now();
        payload.setCreatedAt(now);
        payload.setUpdatedAt(now);
        return salaryRepository.save(payload);
    }

    @Transactional
    public Salary update(Long id, Salary payload) {
        Salary s = salaryRepository.findById(id).orElseThrow(() -> new NotFoundException("Salary not found"));
        if (payload.getAmount() != null) s.setAmount(payload.getAmount());
        if (payload.getPeriod() != null) s.setPeriod(payload.getPeriod());
        if (payload.getStatus() != null) s.setStatus(payload.getStatus());
        s.setUpdatedAt(Instant.now());
        return salaryRepository.save(s);
    }

    @Transactional
    public void delete(Long id) {
        if (!salaryRepository.existsById(id)) throw new NotFoundException("Salary not found");
        salaryRepository.deleteById(id);
    }
}
