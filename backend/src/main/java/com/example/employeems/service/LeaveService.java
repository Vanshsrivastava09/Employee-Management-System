package com.example.employeems.service;

import com.example.employeems.entity.Employee;
import com.example.employeems.entity.LeaveRequest;
import com.example.employeems.exception.NotFoundException;
import com.example.employeems.repository.EmployeeRepository;
import com.example.employeems.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveService(LeaveRequestRepository leaveRepository, EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> listAll() { return leaveRepository.findAll(); }

    @Transactional(readOnly = true)
    public LeaveRequest getById(Long id) { return leaveRepository.findById(id).orElseThrow(() -> new NotFoundException("Leave request not found")); }

    @Transactional
    public LeaveRequest create(Long employeeId, LeaveRequest payload) {
        Employee emp = employeeRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Employee not found"));
        payload.setEmployee(emp);
        payload.setStatus("PENDING");
        Instant now = Instant.now();
        payload.setCreatedAt(now);
        payload.setUpdatedAt(now);
        return leaveRepository.save(payload);
    }

    @Transactional
    public LeaveRequest updateStatus(Long id, String status) {
        LeaveRequest lr = leaveRepository.findById(id).orElseThrow(() -> new NotFoundException("Leave request not found"));
        lr.setStatus(status);
        lr.setUpdatedAt(Instant.now());
        return leaveRepository.save(lr);
    }

    @Transactional
    public void delete(Long id) { if (!leaveRepository.existsById(id)) throw new NotFoundException("Leave request not found"); leaveRepository.deleteById(id); }
}
