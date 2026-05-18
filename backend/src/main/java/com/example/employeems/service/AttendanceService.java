package com.example.employeems.service;

import com.example.employeems.entity.Attendance;
import com.example.employeems.entity.Employee;
import com.example.employeems.exception.NotFoundException;
import com.example.employeems.repository.AttendanceRepository;
import com.example.employeems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Attendance> listAll() { return attendanceRepository.findAll(); }

    @Transactional(readOnly = true)
    public Attendance getById(Long id) { return attendanceRepository.findById(id).orElseThrow(() -> new NotFoundException("Attendance not found")); }

    @Transactional
    public Attendance create(Long employeeId, Attendance payload) {
        Employee emp = employeeRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Employee not found"));
        payload.setEmployee(emp);
        payload.setCreatedAt(Instant.now());
        return attendanceRepository.save(payload);
    }

    @Transactional
    public Attendance update(Long id, Attendance payload) {
        Attendance a = attendanceRepository.findById(id).orElseThrow(() -> new NotFoundException("Attendance not found"));
        if (payload.getDate() != null) a.setDate(payload.getDate());
        if (payload.getStatus() != null) a.setStatus(payload.getStatus());
        if (payload.getNote() != null) a.setNote(payload.getNote());
        return attendanceRepository.save(a);
    }

    @Transactional
    public void delete(Long id) { if (!attendanceRepository.existsById(id)) throw new NotFoundException("Attendance not found"); attendanceRepository.deleteById(id); }
}
