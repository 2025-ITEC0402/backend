package com.ema.ema_backend.domain.chatroom;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.message.Message;
import com.ema.ema_backend.global.BaseEntityWithUpdatedAt;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ChatRoom extends BaseEntityWithUpdatedAt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    private String name;

    // 채팅방 채팅 재조립을 위한 필드로 구상했는데 필요 없을듯
    // private int lastMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages = new ArrayList<>();
}
