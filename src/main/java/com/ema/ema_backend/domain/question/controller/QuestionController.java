package com.ema.ema_backend.domain.question.controller;

import com.ema.ema_backend.domain.question.dto.CheckAnswerRequest;
import com.ema.ema_backend.domain.question.dto.QuestionSet;
import com.ema.ema_backend.domain.question.dto.QuestionsInfoResponse;
import com.ema.ema_backend.domain.question.dto.RecommendedQuestionInfoResponse;
import com.ema.ema_backend.domain.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @Operation(
            summary = "(메인 페이지) 개인 맞춤 문제 추천 조회",
            description = "JWT 인증 토큰을 기반으로 현재 로그인된 회원에게 추천 문제 목록을 제공합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "추천 문제 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecommendedQuestionInfoResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content
                    )
            }
    )
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendedQuestionInfoResponse> getQuestionRecommendations(Authentication authentication) {
        return questionService.getRecommendQuestion(authentication);
    }

    @Operation(
            summary = "(문제 풀이 페이지) 랜덤으로 3문제 받아오기",
            description = "JWT 인증 토큰을 기반으로, 사용자가 아직 풀지 않은 문제 중에서 랜덤으로 3개의 문제를 추천합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "랜덤 문제 추천 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = QuestionsInfoResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content
                    )
            }
    )
    @GetMapping("/random-questions")
    public ResponseEntity<QuestionsInfoResponse> get3RandomQuestions(Authentication authentication) {
        return questionService.get3RandomQuestions(authentication);
    }

    @Operation(
            summary = "회원의 문제 열람 기록 삭제",
            description = "JWT 인증 토큰을 기반으로, 현재 로그인된 회원이 푼 문제(풀이 기록)를 모두 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "문제 풀이 기록 삭제 성공",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/my-questions")
    public ResponseEntity<Void> deleteMyQuestions(Authentication authentication) {
        return questionService.deleteMyQuestions(authentication);
    }

    @Operation(
            summary = "개인화된 문제 1개 생성",
            description = "JWT 인증 토큰을 기반으로, 사용자의 학습 이력 및 추천 정보를 바탕으로 개인화된 문제를 1개 생성합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "개인화 문제 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = QuestionSet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음",
                            content = @Content
                    )
            }
    )
    @PostMapping("/generate")
    public ResponseEntity<QuestionSet> generatePersonalizedQuestion(@RequestParam Integer buttonNum, Authentication authentication) {
        return questionService.generatePersonalizedQuestion(buttonNum, authentication);
    }

    @Operation(
            summary = "id로 문제 조회",
            description = "인증된 사용자가 id에 해당하는 문제를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "문제 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = QuestionSet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음(유효한 JWT 토큰 필요)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음(해당 문제를 조회할 권한이 없음)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 ID의 문제를 찾을 수 없음",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<QuestionSet> getQuestionById(@PathVariable("id") Long id, Authentication authentication) {
        return questionService.getQuestionById(id, authentication);
    }

    @Operation(
            summary = "정답 확인",
            description = "인증된 사용자가 특정 문제에 대해 사용자의 정답 여부를 서버로 전송하여 기록합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "정답 여부 전송 완료(결과 없음)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청(유효하지 않은 파라미터 등)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음(유효한 JWT 토큰 필요)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음(해당 문제를 확인할 권한이 없음)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 ID의 문제를 찾을 수 없음",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content
                    )
            }
    )
    @PostMapping("/{id}")
    public ResponseEntity<Void> checkAnswer(@PathVariable("id") Long id, @RequestBody CheckAnswerRequest req, Authentication authentication) {
        return questionService.checkAnswer(id, req, authentication);
    }
}
