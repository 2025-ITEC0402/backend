package com.ema.ema_backend.domain.question.controller;

import com.ema.ema_backend.domain.question.dto.RecommendedQuestionInfoResponse;
import com.ema.ema_backend.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/recommendations")
    public ResponseEntity<RecommendedQuestionInfoResponse> getQuestionRecommendations(Authentication authentication) {
        return questionService.getRecommendQuestion(authentication);
    }
}
