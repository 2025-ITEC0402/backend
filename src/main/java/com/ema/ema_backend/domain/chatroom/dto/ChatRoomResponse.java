package com.ema.ema_backend.domain.chatroom.dto;

import com.ema.ema_backend.domain.message.dto.MessageSet;

import java.util.List;

public record ChatRoomResponse(Long chatRoomId, String roomTitle, List<MessageSet> messageList) {
}
