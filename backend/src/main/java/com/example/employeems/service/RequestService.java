package com.example.employeems.service;

import com.example.employeems.entity.Employee;
import com.example.employeems.entity.EmployeeRequest;
import com.example.employeems.exception.NotFoundException;
import com.example.employeems.repository.EmployeeRepository;
import com.example.employeems.repository.EmployeeRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class RequestService {

    private final EmployeeRequestRepository requestRepository;
    private final EmployeeRepository employeeRepository;

    public RequestService(EmployeeRequestRepository requestRepository, EmployeeRepository employeeRepository) {
        this.requestRepository = requestRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeRequest> listAll() { return requestRepository.findAll(); }

    @Transactional(readOnly = true)
    public EmployeeRequest getById(Long id) { return requestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request not found")); }

    @Transactional
    public EmployeeRequest create(Long employeeId, EmployeeRequest payload) {
        Employee emp = employeeRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Employee not found"));
        payload.setEmployee(emp);
        payload.setStatus("OPEN");
        Instant now = Instant.now();
        payload.setCreatedAt(now);
        payload.setUpdatedAt(now);
        return requestRepository.save(payload);
    }

    @Transactional
    public EmployeeRequest updateStatus(Long id, String status) {
        EmployeeRequest r = requestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request not found"));
        r.setStatus(status);
        r.setUpdatedAt(Instant.now());
        return requestRepository.save(r);
    }

    @Transactional
    public void delete(Long id) { if (!requestRepository.existsById(id)) throw new NotFoundException("Request not found"); requestRepository.deleteById(id); }
}
