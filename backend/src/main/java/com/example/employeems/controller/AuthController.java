package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.dto.ForgotPasswordRequest;
import com.example.employeems.dto.LoginRequest;
import com.example.employeems.dto.ResetPasswordRequest;
import com.example.employeems.service.AuthService;
import com.example.employeems.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Authentication APIs (session-based).
 *
 * Endpoints:
 * - POST /api/auth/login
 * - POST /api/auth/logout
 * - GET  /api/auth/me
 * - POST /api/auth/forgot-password
 * - POST /api/auth/reset-password
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<Object> login(@Valid @RequestBody LoginRequest request, HttpSession session, HttpServletRequest httpRequest) {
        return authService.login(request, session, httpRequest);
    }

    @PostMapping(value = "/logout", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<Object> logout(HttpSession session) {
        authService.logout(session);
        return ApiResponse.ok("Logged out", null);
    }

    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<Object> me(HttpSession session) {
        boolean loggedIn = authService.isLoggedIn(session);

        HashMap<String, Object> data = new HashMap<>();
        data.put("loggedIn", loggedIn);

        return ApiResponse.ok(loggedIn ? "Authenticated" : "Not authenticated", data);
    }

    @PostMapping(value = "/forgot-password", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // Try admin first, then employee
        try {
            passwordResetService.initiateAdminReset(request.getEmail());
        } catch (Exception e) {
            try {
                passwordResetService.initiateEmployeeReset(request.getEmail());
            } catch (Exception ex) {
                // For security, don't reveal whether email exists
                return ApiResponse.ok("If an account with this email exists, a reset link has been sent", null);
            }
        }
        return ApiResponse.ok("If an account with this email exists, a reset link has been sent", null);
    }

    @PostMapping(value = "/reset-password", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<Object> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String type = request.getType() != null ? request.getType() : "admin";
        
        if ("employee".equalsIgnoreCase(type)) {
            passwordResetService.resetEmployeePassword(request.getToken(), request.getNewPassword());
        } else {
            passwordResetService.resetAdminPassword(request.getToken(), request.getNewPassword());
        }
        
        return ApiResponse.ok("Password has been reset successfully", null);
    }
}
