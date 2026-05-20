package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.dto.LoginRequest;
import com.example.employeems.service.AuthService;
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
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<Object> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        return authService.login(request, session);
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
}
