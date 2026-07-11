package com.example.employeems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Entity class for the employees table.
 *
 * Entity responsibility:
 * - map Java fields to database columns
 * - define relationships, such as Employee -> Department
 * - keep data structure separate from API response DTOs
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Employee full name.
     */
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /**
     * Unique email for each employee.
     */
    @Column(nullable = false, length = 180, unique = true)
    private String email;

    /**
     * Optional phone number.
     */
    @Column(length = 30)
    private String phone;

    /**
     * Each employee belongs to exactly one department.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /**
     * Active/Inactive flag for analytics.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * Stored URL path on backend (example: /api/employees/photos/filename.png)
     */
    @Column(name = "photo_url", length = 300)
    private String photoUrl;

    /**
     * BCrypt hash of password for employee self-service portal.
     */
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    /**
     * Password reset token.
     */
    @Column(name = "reset_token", length = 255)
    private String resetToken;

    /**
     * Password reset token expiry timestamp.
     */
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Employee() {
    }

    // ---------- Getters & Setters ----------
    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Department getDepartment() {
        return department;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getResetToken() {
        return resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
