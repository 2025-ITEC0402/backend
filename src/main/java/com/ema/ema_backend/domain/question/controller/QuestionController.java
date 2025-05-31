package com.ema.ema_backend.domain.question.controller;

import com.ema.ema_backend.domain.question.dto.QuestionSet;
import com.ema.ema_backend.domain.question.dto.QuestionsInfoResponse;
import com.ema.ema_backend.domain.question.dto.RecommendedQuestionInfoResponse;
import com.ema.ema_backend.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/recommendations")
    public ResponseEntity<RecommendedQuestionInfoResponse> getQuestionRecommendations(Authentication authentication) {
        return questionService.getRecommendQuestion(authentication);
    }

    @GetMapping("/random-questions")
    public ResponseEntity<QuestionsInfoResponse> get3RandomQuestions(Authentication authentication) {
        return questionService.get3RandomQuestions(authentication);
    }

    @DeleteMapping("/my-questions")
    public ResponseEntity<Void> deleteMyQuestions(Authentication authentication) {
        return questionService.deleteMyQuestions(authentication);
    }

    @PostMapping("/generate")
    public ResponseEntity<QuestionSet> generatePersonalizedQuestion(Authentication authentication) {
        return questionService.generatePersonalizedQuestion(authentication);
    }
}
