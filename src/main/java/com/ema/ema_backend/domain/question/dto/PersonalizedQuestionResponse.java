package com.ema.ema_backend.domain.question.dto;

public record PersonalizedQuestionResponse(String chapter, String question, String choice1, String choice2, String choice3, String choice4, Integer answer, String solution, String difficulty, String ai_summary) {
}