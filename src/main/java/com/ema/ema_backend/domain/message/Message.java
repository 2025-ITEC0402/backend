package com.ema.ema_backend.domain.message;

import com.ema.ema_backend.domain.chatroom.ChatRoom;
import com.ema.ema_backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    // 채팅 메세지 재조립을 위한 필드로 구상했으나 필요 없을듯
    // private int order;

    private SenderType senderType;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat room_id")
    private ChatRoom chatRoom;

    public Message(String role, String content, ChatRoom chatRoom) {
        this.senderType = SenderType.getSenderType(role);
        this.content = content;
        this.chatRoom = chatRoom;
    }
}
