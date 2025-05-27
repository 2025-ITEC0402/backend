package com.ema.ema_backend.domain.message;

import com.ema.ema_backend.domain.chatroom.ChatRoom;
import com.ema.ema_backend.global.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Message extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    // 채팅 메세지 재조립을 위한 필드로 구상했으나 필요 없을듯
    // private int order;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat room_id")
    private ChatRoom chatRoom;
}
