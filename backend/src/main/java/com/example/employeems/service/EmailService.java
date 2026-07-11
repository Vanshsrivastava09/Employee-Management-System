package com.example.employeems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Email service for sending password reset emails.
 * 
 * Uses SMTP configuration from environment variables.
 * In production, configure real SMTP credentials.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.smtp.host:}")
    private String smtpHost;

    @Value("${app.smtp.port:587}")
    private String smtpPort;

    @Value("${app.smtp.username:}")
    private String smtpUsername;

    @Value("${app.smtp.password:}")
    private String smtpPassword;

    @Value("${app.smtp.from:noreply@employeems.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:5500}")
    private String frontendUrl;

    /**
     * Send password reset email to user.
     * 
     * In development, this logs the reset link instead of sending email.
     * In production, configure SMTP credentials to send real emails.
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken, String userType) {
        String resetLink = String.format("%s/reset-password.html?token=%s&type=%s", frontendUrl, resetToken, userType);
        
        logger.info("Password reset link for {}: {}", toEmail, resetLink);
        
        // In production, implement actual email sending using JavaMail API
        // For now, this is a placeholder that logs the link
        if (smtpHost != null && !smtpHost.isEmpty()) {
            // TODO: Implement actual email sending with JavaMail
            logger.info("SMTP configured - would send email to: {}", toEmail);
        } else {
            logger.info("SMTP not configured - reset link available in logs for development");
        }
    }
}
