package com.ema.ema_backend.domain.message.dto;

import com.ema.ema_backend.domain.message.Message;

import java.time.LocalDateTime;

public record MessageSet(Long messageId, String senderType, String content, LocalDateTime createdAt) {
    public static MessageSet from(Message m){
        return new MessageSet(m.getId(), m.getSenderType().toString(), m.getContent(), m.getCreatedAt());
    }
}
