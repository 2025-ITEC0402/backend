package com.ema.ema_backend.domain.chatroom.dto;

import java.time.LocalDateTime;

public record FirstChatResponse(Long chatroom_id, String roomTitle, String role, String answer, LocalDateTime created_at) {
}
