package com.ema.ema_backend.domain.member.entity;

import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.domain.type.LearningLevelType;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;

@Entity
public class LearningHistory extends BaseEntityWithUpdatedAt {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "learning_history_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChapterType recommendedChapter1;
    @Enumerated(EnumType.STRING)
    private ChapterType recommendedChapter2;
    @Enumerated(EnumType.STRING)
    private ChapterType recommendedChapter3;
    @Enumerated(EnumType.STRING)
    private LearningLevelType learningLevel;

    private String goal;
}
