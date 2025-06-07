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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    @Lob
    private String content;

    /**
     * 이미지 메시지 Data URI (Base64 인코딩 문자열 포함) 저장 필드
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data_uri", columnDefinition = "LONGTEXT")
    private String imageDataUri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public static Message ofText(String role, String content, ChatRoom chatRoom) {
        Message m = new Message();
        m.senderType = SenderType.getSenderType(role);
        m.content = content;
        m.chatRoom = chatRoom;
        return m;
    }

    /**
     * 이미지 메시지 생성자
     * MultipartFile을 Base64로 인코딩하여 Data URI 형식으로 저장
     */
    public static Message ofImage(String role,
                                  String imageDataUri,
                                  String content,
                                  ChatRoom chatRoom) {
        Message m = new Message();
        m.senderType = SenderType.getSenderType(role);
        m.imageDataUri = imageDataUri;
        // 텍스트 메시지도 함께 저장할 경우
        m.content = content;
        m.chatRoom = chatRoom;
        return m;
    }
}