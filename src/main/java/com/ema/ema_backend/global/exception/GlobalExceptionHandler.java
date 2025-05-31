package com.ema.ema_backend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ProblemDetail> handleBadResponseException(ExternalApiException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, e.getMessage());
        problemDetail.setTitle("BAD_GATEWAY");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(problemDetail);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadResponseException(BadRequestException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("BAD_REQUEST");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ProblemDetail> handleBadResponseException(TokenExpiredException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setTitle("ACCESS_TOKEN_EXPIRED");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleMemberNotFoundException(MemberNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("MEMBER_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
}
