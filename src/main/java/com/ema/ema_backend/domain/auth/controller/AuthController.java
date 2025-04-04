package com.ema.ema_backend.domain.auth.controller;

import com.ema.ema_backend.domain.auth.dto.KakaoLoginRequest;
import com.ema.ema_backend.domain.auth.dto.TokenRefreshRequest;
import com.ema.ema_backend.domain.auth.dto.TokenDto;
import com.ema.ema_backend.domain.auth.jwt.JwtProvider;
import com.ema.ema_backend.domain.auth.service.AuthService;
import com.ema.ema_backend.global.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
                    @ApiResponse(responseCode = "200", description = "로그인 성공. 리프레시 토큰은 쿠키에",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"accessToken\": \"your-access-token\"}"))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
            }
    )
    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody KakaoLoginRequest request, HttpServletResponse response) {
        TokenDto tokenDto = authService.kakaoLogin(request.getCode());
        addJwtCookie(tokenDto.getRefreshToken(), response);

            return ResponseEntity.ok().body(Map.of("accessToken", tokenDto.getAccessToken()));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "리프레시 토큰을 이용해 새로운 액세스 토큰을 재발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공. 리프레시 토큰은 쿠키에",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"accessToken\": \"your-access-token\"}"))),
                    @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료 또는 유효하지 않음", content = @Content)
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenByCookie(request);

        TokenDto tokenDto = jwtProvider.refreshAccessToken(refreshToken);

        addJwtCookie(tokenDto.getRefreshToken(), response);

        return ResponseEntity.ok().body(Map.of("accessToken", tokenDto.getAccessToken()));
    }

    private void addJwtCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); //7일
        response.addCookie(cookie);
    }

    private String getRefreshTokenByCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new BadRequestException("refreshToken이 없습니다. 다시 로그인 하세요.");
    }
}
