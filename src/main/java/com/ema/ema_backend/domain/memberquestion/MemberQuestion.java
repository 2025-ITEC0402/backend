package com.ema.ema_backend.domain.memberquestion;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.question.Question;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberQuestion extends BaseEntityWithUpdatedAt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime solvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public MemberQuestion(Member member, Question question) {
        this.member = member;
        this.question = question;
        this.solvedAt = LocalDateTime.now();
    }

    public void removeFromBothSides() {
        if (member != null) {
            member.getMemberQuestions().remove(this);
            this.member = null;
        }
        if (question != null) {
            question.getQuestionList().remove(this);
            this.question = null;
        }
    }
}
