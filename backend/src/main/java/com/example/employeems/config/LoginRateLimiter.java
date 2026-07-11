package com.example.employeems.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory rate limiter for login attempts
 * 
 * Prevents brute force attacks by limiting login attempts per IP address.
 * This is a simple implementation that stores attempt counts in memory.
 * For production, consider using Redis or a database-backed solution.
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    
    private final ConcurrentMap<String, AttemptRecord> attemptRecords = new ConcurrentHashMap<>();

    public boolean isAllowed(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        AttemptRecord record = attemptRecords.get(clientIp);
        
        if (record == null) {
            attemptRecords.put(clientIp, new AttemptRecord());
            return true;
        }
        
        // Check if lockout period has expired
        if (record.isLockedOut() && System.currentTimeMillis() > record.getLockoutEndTime()) {
            record.reset();
            return true;
        }
        
        // Check if currently locked out
        if (record.isLockedOut()) {
            return false;
        }
        
        // Increment attempt count
        record.incrementAttempts();
        
        // Lock out if max attempts reached
        if (record.getAttempts() >= MAX_ATTEMPTS) {
            record.lockOut();
            return false;
        }
        
        return true;
    }

    public void recordSuccessfulLogin(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        AttemptRecord record = attemptRecords.get(clientIp);
        if (record != null) {
            record.reset();
        }
    }

    public long getRemainingLockoutTime(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        AttemptRecord record = attemptRecords.get(clientIp);
        if (record == null || !record.isLockedOut()) {
            return 0;
        }
        return Math.max(0, record.getLockoutEndTime() - System.currentTimeMillis());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs in X-Forwarded-For (take the first one)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private static class AttemptRecord {
        private final AtomicInteger attempts = new AtomicInteger(0);
        private long lockoutEndTime = 0;

        public void incrementAttempts() {
            attempts.incrementAndGet();
        }

        public int getAttempts() {
            return attempts.get();
        }

        public void reset() {
            attempts.set(0);
            lockoutEndTime = 0;
        }

        public void lockOut() {
            lockoutEndTime = System.currentTimeMillis() + LOCKOUT_DURATION_MS;
        }

        public boolean isLockedOut() {
            return lockoutEndTime > 0 && System.currentTimeMillis() < lockoutEndTime;
        }

        public long getLockoutEndTime() {
            return lockoutEndTime;
        }
    }
}
