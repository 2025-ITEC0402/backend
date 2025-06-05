package com.ema.ema_backend.global.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resourceName, String message) {
        super(String.format("%s not found %s", resourceName, message));
    }
}
