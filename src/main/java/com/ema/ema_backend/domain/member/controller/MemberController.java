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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            description = "JWT 인증 토큰을 기반으로 현재 로그인된 회원의 연속 학습 정보, 푼 문제 수 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "메인 페이지 상단 학습 정보 조회 성공",
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

    @Operation(
            summary = "학습 단원 완료 처리", // API의 핵심 기능을 한 줄로 요약
            description = """
    로그인한 사용자가 특정 단원의 학습을 완료했음을 기록합니다.
    요청 시 Authorization 헤더에 유효한 Bearer 토큰이 포함되어야 합니다.
    """ // API에 대한 상세 설명
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "단원 완료 처리 성공",
                    content = @Content(schema = @Schema(implementation = Void.class)) // 성공 시 반환값이 없음을 명시
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 chapterId 요청",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content
            )
    })
    @PostMapping("/complete")
    public ResponseEntity<Void> postCompletedChapter(@RequestParam("chapterId") Integer chapterId, Authentication authentication) {
        return memberService.postCompletedChapter(chapterId, authentication);
    }
}
