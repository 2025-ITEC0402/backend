package com.ema.ema_backend.domain.member.entity;

import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.domain.type.LearningLevelType;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
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

    @ElementCollection(fetch = FetchType.LAZY) // LAZY 로딩을 권장합니다.
    @CollectionTable(
            name = "completed_chapter", // 생성될 테이블의 이름
            joinColumns = @JoinColumn(name = "learning_history_id") // LearningHistory를 참조하는 외래 키
    )
    @Column(name = "chapter_number") // 컬렉션에 저장될 값의 컬럼 이름
    private Set<Integer> completedChapters = new HashSet<>();

    @Setter
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public LearningHistory() {
        this.recommendedChapter1 = ChapterType.CHAPTER_1;
        this.recommendedChapter2 = ChapterType.CHAPTER_2;
        this.recommendedChapter3 = ChapterType.CHAPTER_3;
        this.learningLevel = LearningLevelType.BEGINNER;
    }

    public void update(String chapter1, String chapter2, String chapter3, String goal, Member member) {
        this.recommendedChapter1 = ChapterType.fromStringNumber(chapter1);
        this.recommendedChapter2 = ChapterType.fromStringNumber(chapter2);
        this.recommendedChapter3 = ChapterType.fromStringNumber(chapter3);
        this.goal = goal;
        this.member = member;
    }
}
