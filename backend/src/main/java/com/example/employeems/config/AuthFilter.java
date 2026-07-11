package com.example.employeems.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Beginner-friendly authentication guard.
 *
 * Rules:
 * - Allow all requests to /api/auth/** (login/logout/me)
 * - Require admin session for all other /api/** calls
 *
 * This keeps the project easy to understand (no JWT).
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final String SESSION_ADMIN_ID = "ADMIN_ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only protect REST APIs
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Public endpoints
        if (path.startsWith("/api/auth/") || path.equals("/api/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute(SESSION_ADMIN_ID) != null;

        if (!loggedIn) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
