package com.ema.ema_backend.domain.question.repository;

import com.ema.ema_backend.domain.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
