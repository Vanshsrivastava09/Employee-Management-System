package com.example.employeems.repository;

import com.example.employeems.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployee_Id(Long employeeId);
}
