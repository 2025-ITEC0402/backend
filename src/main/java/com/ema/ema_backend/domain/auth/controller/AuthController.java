package com.ema.ema_backend.domain.auth.controller;

import com.ema.ema_backend.domain.auth.dto.TokenResponse;
import com.ema.ema_backend.domain.member.service.MemberService;
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
    private final MemberService memberService;

    @GetMapping("/oauth/kakao/login")
    public ResponseEntity<TokenResponse> login(@RequestParam("code") String code) {
        TokenResponse tokenResponse = memberService.kakaoLogin(code);

        return ResponseEntity.ok().body(tokenResponse);
    }
}
