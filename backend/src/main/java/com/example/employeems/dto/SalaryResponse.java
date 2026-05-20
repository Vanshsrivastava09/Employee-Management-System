package com.example.employeems.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class SalaryResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private BigDecimal amount;
    private String period;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public SalaryResponse() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
