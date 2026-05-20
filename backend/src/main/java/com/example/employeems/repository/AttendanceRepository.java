package com.example.employeems.repository;

import com.example.employeems.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployee_Id(Long employeeId);
    List<Attendance> findByDate(LocalDate date);
}
