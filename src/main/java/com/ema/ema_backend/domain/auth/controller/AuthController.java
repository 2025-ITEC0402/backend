package com.ema.ema_backend.domain.auth.controller;

import com.ema.ema_backend.domain.auth.dto.KakaoLoginRequest;
import com.ema.ema_backend.domain.auth.dto.TokenRefreshRequest;
import com.ema.ema_backend.domain.auth.dto.TokenResponse;
import com.ema.ema_backend.domain.auth.jwt.JwtProvider;
import com.ema.ema_backend.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "OAuth 및 토큰 관련 API")
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @Operation(
            summary = "카카오 로그인",
            description = "카카오 인가 코드를 통해 로그인하고 액세스/리프레시 토큰을 발급받습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
            }
    )
    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<TokenResponse> login(@RequestBody KakaoLoginRequest request) {
        TokenResponse tokenResponse = authService.kakaoLogin(request.getCode());
        return ResponseEntity.ok().body(tokenResponse);
    }

    @Operation(
            summary = "토큰 재발급",
            description = "리프레시 토큰을 이용해 새로운 액세스 토큰을 재발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료 또는 유효하지 않음", content = @Content)
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRefreshRequest request) {
        TokenResponse tokenResponse = jwtProvider.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok().body(tokenResponse);
    }

}
