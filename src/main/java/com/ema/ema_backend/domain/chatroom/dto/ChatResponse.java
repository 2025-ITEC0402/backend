package com.ema.ema_backend.domain.chatroom.dto;

import java.time.LocalDateTime;

public record ChatResponse(Long chatroom_id, String role, String answer, LocalDateTime created_at) {
}
