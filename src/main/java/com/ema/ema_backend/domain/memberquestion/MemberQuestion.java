package com.ema.ema_backend.domain.memberquestion;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.question.Question;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
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
}
