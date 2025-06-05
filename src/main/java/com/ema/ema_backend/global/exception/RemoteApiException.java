package com.ema.ema_backend.global.exception;

public class RemoteApiException extends RuntimeException {
    public RemoteApiException(String message) {
        super(message);
    }
}
