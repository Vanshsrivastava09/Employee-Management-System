package com.example.employeems.exception;

import com.example.employeems.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Central exception handler to keep API error responses consistent.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        logger.warn("Not found: {} - {}", request.getRequestURI(), ex.getMessage());
        ApiResponse<Object> body = ApiResponse.fail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        logger.warn("Bad request: {} - {}", request.getRequestURI(), ex.getMessage());
        ApiResponse<Object> body = ApiResponse.fail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Fallback handler for unexpected errors.
     * Logs the real exception details server-side for debugging.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        logger.error("Unexpected error: {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        ApiResponse<Object> body = ApiResponse.fail("Something went wrong");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
