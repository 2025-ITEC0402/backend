package com.ema.ema_backend.domain.member.entity;

import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.domain.type.LearningLevelType;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class LearningHistory extends BaseEntityWithUpdatedAt {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "learning_history_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChapterType recommendedChapter1 = ChapterType.INITIAL;
    @Enumerated(EnumType.STRING)
    private ChapterType recommendedChapter2 = ChapterType.INITIAL;
    @Enumerated(EnumType.STRING)
    private ChapterType recommendedChapter3 = ChapterType.INITIAL;
    @Enumerated(EnumType.STRING)
    private LearningLevelType learningLevel = LearningLevelType.INITIAL;

    private String goal;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
