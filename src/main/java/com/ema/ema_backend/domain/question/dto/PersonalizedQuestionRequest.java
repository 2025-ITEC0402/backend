package com.ema.ema_backend.domain.question.dto;

public record PersonalizedQuestionRequest(String topics, String range, String summarized, String difficulty, String quiz_examples, String query) {
}
