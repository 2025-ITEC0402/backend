package com.ema.ema_backend.domain.chatroom.repository;

import com.ema.ema_backend.domain.chatroom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
