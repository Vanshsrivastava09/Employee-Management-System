package com.example.employeems.service;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.dto.LoginRequest;
import com.example.employeems.entity.AdminUser;
import com.example.employeems.repository.AdminUserRepository;
import com.example.employeems.exception.BadRequestException;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Simple session-based authentication for a fresher-friendly project.
 *
 * - User logs in with username + password
 * - Backend verifies password using BCrypt (seeded at startup)
 * - If valid: store admin id in HttpSession
 *
 * NOTE:
 * This is intentionally simple and easy to explain in interviews (no JWT).
 */
@Service
public class AuthService {

    private static final String SESSION_ADMIN_ID = "ADMIN_ID";

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Validates login credentials and stores admin id in session.
     */
    public ApiResponse<Object> login(LoginRequest request, HttpSession session) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        String username = safeTrim(request.getUsername());
        String password = request.getPassword();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BadRequestException("Username and password are required");
        }

        AdminUser admin = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        boolean matches = passwordEncoder.matches(password, admin.getPasswordHash());
        if (!matches) {
            throw new BadRequestException("Invalid username or password");
        }

        session.setAttribute(SESSION_ADMIN_ID, admin.getId());

        return ApiResponse.ok("Login successful", null);
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Checks if admin is logged in.
     */
    public boolean isLoggedIn(HttpSession session) {
        if (session == null) return false;
        Object adminId = session.getAttribute(SESSION_ADMIN_ID);
        return adminId != null;
    }

    public Long getLoggedInAdminId(HttpSession session) {
        if (session == null) return null;
        Object adminId = session.getAttribute(SESSION_ADMIN_ID);
        if (adminId instanceof Long) return (Long) adminId;
        return null;
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }
}
