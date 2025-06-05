package com.ema.ema_backend.domain.chatroom.dto;

import java.util.List;

public record ChatRoomInfoResponse(List<ChatRoomWithoutMessagesResponse> chatRoomInfos) {
}
