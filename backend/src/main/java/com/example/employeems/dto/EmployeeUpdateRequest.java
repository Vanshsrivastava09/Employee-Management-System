package com.example.employeems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating an employee.
 * All fields are optional except departmentId (required to keep model simple).
 */
public class EmployeeUpdateRequest {

    @Size(max = 150)
    private String fullName;

    @Email
    @Size(max = 180)
    private String email;

    @Size(max = 30)
    private String phone;

    private Long departmentId;

    private boolean isActive;

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
