package com.ema.ema_backend.domain.member.controller;

import com.ema.ema_backend.domain.auth.dto.TokenResponse;
import com.ema.ema_backend.domain.member.dto.MemberInfoResponse;
import com.ema.ema_backend.domain.member.dto.StreakInfoResponse;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "사용자 인증 확인",
            description = "JWT 인증 토큰을 기반으로 현재 사용자의 인증/인가 상태를 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "인증 확인 성공",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content
                    )
            }
    )
    @GetMapping("/check")
    public ResponseEntity<String> checkPermission(Authentication authentication) {
        return memberService.testCheckPermission(authentication);
    }

    @Operation(
            summary = "(메인 페이지) 회원 정보 조회",
            description = "JWT 인증 토큰을 기반으로 현재 로그인된 회원의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberInfoResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content
                    )
            }
    )
    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(Authentication authentication) {
        return memberService.getMemberInfo(authentication);
    }

    @Operation(
            summary = "(메인 페이지) 연속 학습 정보 조회",
            description = "JWT 인증 토큰을 기반으로 현재 로그인된 회원의 연속 학습(스트릭) 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "스트릭 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StreakInfoResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content
                    )
            }
    )
    @GetMapping("/streak")
    public ResponseEntity<StreakInfoResponse> getStreakInfo(Authentication authentication) {
        return memberService.getStreakInfo(authentication);
    }

    @Operation(
            summary = "(메인 페이지) 학습 이력 갱신",
            description = "JWT 인증 토큰을 기반으로 로그인된 회원의 학습 이력을 갱신합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "학습 이력 갱신 성공",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content
                    )
            }
    )
    @PutMapping("/learning-history")
    public ResponseEntity<Void> updateLearningHistory(Authentication authentication) {
        return memberService.updateLearningHistory(authentication);
    }
}
