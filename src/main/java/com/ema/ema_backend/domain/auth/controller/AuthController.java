package com.ema.ema_backend.domain.auth.controller;

import com.ema.ema_backend.domain.auth.dto.TokenResponse;
import com.ema.ema_backend.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/oauth/kakao/login")
    public ResponseEntity<TokenResponse> login(@RequestParam("code") String code) {
        TokenResponse tokenResponse = authService.kakaoLogin(code);

        return ResponseEntity.ok().body(tokenResponse);
    }
}
