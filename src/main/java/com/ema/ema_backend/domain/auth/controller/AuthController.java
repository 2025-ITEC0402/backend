package com.ema.ema_backend.domain.auth.controller;

import com.ema.ema_backend.domain.auth.dto.KakaoLoginRequest;
import com.ema.ema_backend.domain.auth.dto.TokenRefreshRequest;
import com.ema.ema_backend.domain.auth.dto.TokenResponse;
import com.ema.ema_backend.domain.auth.jwt.JwtProvider;
import com.ema.ema_backend.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<TokenResponse> login(@RequestBody KakaoLoginRequest request) {
        TokenResponse tokenResponse = authService.kakaoLogin(request.getCode());

        return ResponseEntity.ok().body(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRefreshRequest request) {
        TokenResponse tokenResponse = jwtProvider.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok().body(tokenResponse);
    }

}
