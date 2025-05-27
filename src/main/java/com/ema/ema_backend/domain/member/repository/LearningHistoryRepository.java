package com.ema.ema_backend.domain.member.repository;

import com.ema.ema_backend.domain.member.entity.LearningHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningHistoryRepository extends JpaRepository<LearningHistory, Long> {
}
