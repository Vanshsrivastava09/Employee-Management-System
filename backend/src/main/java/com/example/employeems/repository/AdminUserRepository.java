package com.example.employeems.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employeems.entity.AdminUser;

/**
 * Repository for admin users.
 * Used by AuthService to validate login.
 */
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
    Optional<AdminUser> findByEmail(String email);
    Optional<AdminUser> findByResetToken(String resetToken);
}
