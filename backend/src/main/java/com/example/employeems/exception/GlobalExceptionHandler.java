package com.example.employeems.exception;

import com.example.employeems.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Central exception handler to keep API error responses consistent.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        ApiResponse<Object> body = ApiResponse.fail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        ApiResponse<Object> body = ApiResponse.fail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Fallback handler for unexpected errors.
     * In production, log the real exception details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiResponse<Object> body = ApiResponse.fail("Something went wrong");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
