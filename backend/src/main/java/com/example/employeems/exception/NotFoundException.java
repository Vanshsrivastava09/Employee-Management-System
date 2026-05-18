package com.example.employeems.exception;

/**
 * Simple 404 style exception for missing resources.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
