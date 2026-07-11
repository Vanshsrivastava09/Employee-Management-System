package com.example.employeems.service;

import com.example.employeems.entity.AdminUser;
import com.example.employeems.entity.Employee;
import com.example.employeems.exception.BadRequestException;
import com.example.employeems.repository.AdminUserRepository;
import com.example.employeems.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Service for handling password reset functionality.
 * Supports both admin users and employees.
 */
@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_EXPIRY_MINUTES = 30;
    private static final int TOKEN_LENGTH = 32;

    private final AdminUserRepository adminUserRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            AdminUserRepository adminUserRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.adminUserRepository = adminUserRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Initiate password reset for admin user.
     */
    public void initiateAdminReset(String email) {
        AdminUser admin = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No admin account found with this email"));

        String token = generateToken();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES);

        admin.setResetToken(token);
        admin.setResetTokenExpiry(expiry);
        adminUserRepository.save(admin);

        emailService.sendPasswordResetEmail(email, token, "admin");
        logger.info("Password reset initiated for admin: {}", email);
    }

    /**
     * Initiate password reset for employee.
     */
    public void initiateEmployeeReset(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No employee found with this email"));

        String token = generateToken();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES);

        employee.setResetToken(token);
        employee.setResetTokenExpiry(expiry);
        employeeRepository.save(employee);

        emailService.sendPasswordResetEmail(email, token, "employee");
        logger.info("Password reset initiated for employee: {}", email);
    }

    /**
     * Reset password for admin user using token.
     */
    public void resetAdminPassword(String token, String newPassword) {
        AdminUser admin = adminUserRepository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (admin.getResetTokenExpiry() == null || admin.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token has expired");
        }

        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        admin.setResetToken(null);
        admin.setResetTokenExpiry(null);
        adminUserRepository.save(admin);

        logger.info("Password reset completed for admin: {}", admin.getEmail());
    }

    /**
     * Reset password for employee using token.
     */
    public void resetEmployeePassword(String token, String newPassword) {
        Employee employee = employeeRepository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (employee.getResetTokenExpiry() == null || employee.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token has expired");
        }

        employee.setPasswordHash(passwordEncoder.encode(newPassword));
        employee.setResetToken(null);
        employee.setResetTokenExpiry(null);
        employeeRepository.save(employee);

        logger.info("Password reset completed for employee: {}", employee.getEmail());
    }

    /**
     * Generate a secure random token.
     */
    private String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
