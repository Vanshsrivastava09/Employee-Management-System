package com.example.employeems.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Request payload for creating salary records.
 */
public class SalaryCreateRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 60, message = "Period must not exceed 60 characters")
    private String period;

    @Size(max = 40, message = "Status must not exceed 40 characters")
    private String status;

    public Long getEmployeeId() {
        return employeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPeriod() {
        return period;
    }

    public String getStatus() {
        return status;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
