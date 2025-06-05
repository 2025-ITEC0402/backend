package com.ema.ema_backend.global.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String resourceName, String message) {
        super(String.format("Unauthorized access to %s resource %s", resourceName, message));
    }
}
