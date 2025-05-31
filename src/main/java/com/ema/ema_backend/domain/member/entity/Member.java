package com.ema.ema_backend.domain.member.entity;

import com.ema.ema_backend.domain.chatroom.ChatRoom;
import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntityWithUpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Setter
    @OneToOne
    @JoinColumn(name = "learning_history_id")
    private LearningHistory learningHistory;

    @OneToMany(fetch = FetchType.LAZY)
    private final List<MemberQuestion> memberQuestions = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private final List<ChatRoom> chatRooms = new ArrayList<>();

    @Builder
    private Member(String name, String email, LearningHistory learningHistory) {
        this.name = name;
        this.email = email;
        this.learningHistory = learningHistory;
    }
    public static Member createByNameAndEmail(String name, String email, LearningHistory learningHistory) {
        return new Member(name, email, learningHistory);
    }
}
