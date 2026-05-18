package com.example.employeems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Admin user used for simple session-based authentication.
 *
 * For fresher-friendly explanation:
 * - User logs in via /api/auth/login with username+password
 * - Backend validates password and then marks admin as logged in by storing adminId in HttpSession.
 */
@Entity
@Table(name = "admin_users")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 60, unique = true)
    private String username;

    /**
     * BCrypt hash of password.
     */
    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    public AdminUser() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
