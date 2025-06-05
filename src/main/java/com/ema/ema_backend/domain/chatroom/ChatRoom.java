package com.ema.ema_backend.domain.chatroom;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.message.Message;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntityWithUpdatedAt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Setter
    private String roomTitle;

    // 채팅방 채팅 재조립을 위한 필드로 구상했는데 필요 없을듯
    // private int lastMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages = new ArrayList<>();

    public ChatRoom(String roomTitle, Member member) {
        this.roomTitle = roomTitle;
        this.member = member;
    }

}
