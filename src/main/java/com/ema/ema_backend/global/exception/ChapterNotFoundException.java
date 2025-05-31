package com.ema.ema_backend.global.exception;

public class ChapterNotFoundException extends RuntimeException {
    public ChapterNotFoundException(String message) {
        super(message);
    }
}
