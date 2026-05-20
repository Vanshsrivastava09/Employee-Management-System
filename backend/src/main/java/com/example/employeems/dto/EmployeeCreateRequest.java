package com.example.employeems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for adding an employee.
 *
 * Beginner-friendly: Keep only what frontend needs.
 */
public class EmployeeCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 180)
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 30)
    private String phone;

    @NotNull(message = "Department id is required")
    private Long departmentId;

    private boolean isActive;

    /**
     * If frontend uploads image using multipart/form-data,
     * we don't store the file content in DTO. Instead photo comes as file.
     * This field can be used if you want to set an existing URL later.
     */
    private String photoUrl;

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
