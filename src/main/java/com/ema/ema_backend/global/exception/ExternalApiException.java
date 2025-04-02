package com.ema.ema_backend.global.exception;

public class ExternalApiException extends RuntimeException{
    public ExternalApiException(String message){
        super(message);
    }
}
