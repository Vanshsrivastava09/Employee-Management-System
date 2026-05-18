package com.example.employeems.exception;

/**
 * Used when client sends invalid input.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
