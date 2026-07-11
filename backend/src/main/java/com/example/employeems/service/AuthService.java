package com.example.employeems.service;

import com.example.employeems.config.LoginRateLimiter;
import com.example.employeems.dto.ApiResponse;
import com.example.employeems.dto.LoginRequest;
import com.example.employeems.entity.AdminUser;
import com.example.employeems.repository.AdminUserRepository;
import com.example.employeems.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Simple session-based authentication for a fresher-friendly project.
 *
 * - User logs in with username + password
 * - Backend verifies password using BCrypt (seeded at startup)
 * - If valid: store admin id in HttpSession
 * - Rate limiting prevents brute force attacks
 *
 * NOTE:
 * This is intentionally simple and easy to explain in interviews (no JWT).
 */
@Service
public class AuthService {

    private static final String SESSION_ADMIN_ID = "ADMIN_ID";

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginRateLimiter rateLimiter;

    public AuthService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder, LoginRateLimiter rateLimiter) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Validates login credentials and stores admin id in session.
     * Includes rate limiting to prevent brute force attacks.
     */
    public ApiResponse<Object> login(LoginRequest request, HttpSession session, HttpServletRequest httpRequest) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        // Check rate limiting
        if (!rateLimiter.isAllowed(httpRequest)) {
            long remainingTime = rateLimiter.getRemainingLockoutTime(httpRequest);
            long minutesRemaining = (remainingTime / 1000 / 60) + 1;
            throw new BadRequestException("Too many login attempts. Please try again in " + minutesRemaining + " minutes.");
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

        // Record successful login and reset rate limit
        rateLimiter.recordSuccessfulLogin(httpRequest);
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
