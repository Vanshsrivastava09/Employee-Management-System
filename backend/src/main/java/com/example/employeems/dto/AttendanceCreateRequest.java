package com.example.employeems.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request payload for creating attendance records.
 */
public class AttendanceCreateRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private LocalDate date;

    @Size(max = 40, message = "Status must not exceed 40 characters")
    private String status;

    @Size(max = 250, message = "Note must not exceed 250 characters")
    private String note;

    public Long getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
