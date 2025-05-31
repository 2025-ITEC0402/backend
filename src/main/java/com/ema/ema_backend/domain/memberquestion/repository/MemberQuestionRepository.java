package com.ema.ema_backend.domain.memberquestion.repository;

import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberQuestionRepository extends JpaRepository<MemberQuestion, Long> {
}
