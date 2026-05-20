package com.example.employeems.repository;

import com.example.employeems.entity.EmployeeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRequestRepository extends JpaRepository<EmployeeRequest, Long> {
    List<EmployeeRequest> findByEmployee_Id(Long employeeId);
}
