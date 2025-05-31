package com.ema.ema_backend.domain.member.entity;

import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.domain.type.LearningLevelType;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public LearningHistory() {
        this.recommendedChapter1 = ChapterType.INITIAL;
        this.recommendedChapter2 = ChapterType.INITIAL;
        this.recommendedChapter3 = ChapterType.INITIAL;
        this.learningLevel = LearningLevelType.INITIAL;
    }
}
