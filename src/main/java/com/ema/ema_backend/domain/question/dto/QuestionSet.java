package com.ema.ema_backend.domain.question.dto;

public record QuestionSet(
        Long question_id,
        String title,
        String choice1,
        String choice2,
        String choice3,
        String choice4,
        String answer,
        String explaination,
        String difficulty,
        String chapter
        ) {
}
