package com.ema.ema_backend.domain.question;

import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.domain.type.DifficultyType;
import com.ema.ema_backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Question extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;

    private String answer;

    private String explanation;
    private String aiSummary;

    @Enumerated(EnumType.STRING)
    private DifficultyType difficulty;

    @Enumerated(EnumType.STRING)
    private ChapterType chapter;

    @OneToMany(mappedBy = "question")
    private List<MemberQuestion> questionList = new ArrayList<>();

    @Builder
    public Question(String title,
                    String choice1,
                    String choice2,
                    String choice3,
                    String choice4,
                    String answer,
                    String explanation,
                    String aiSummary,
                    DifficultyType difficultyType,
                    ChapterType chapterType) {
        this.title = title;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.answer = answer;
        this.explanation = explanation;
        this.aiSummary = aiSummary;
        this.difficulty = difficultyType;
        this.chapter = chapterType;
    }
}
