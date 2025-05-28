package com.ema.ema_backend.domain.question;

import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.domain.type.DifficultyType;
import com.ema.ema_backend.global.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
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

    @Enumerated(EnumType.STRING)
    private DifficultyType difficulty;

    @Enumerated(EnumType.STRING)
    private ChapterType chapter;

    @OneToMany(mappedBy = "question")
    private List<MemberQuestion> questionList = new ArrayList<>();

}
