package com.example.employeems.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request payload for creating leave requests.
 */
public class LeaveCreateRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    public Long getEmployeeId() {
        return employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
