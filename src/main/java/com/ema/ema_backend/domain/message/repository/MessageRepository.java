package com.ema.ema_backend.domain.message.repository;

import com.ema.ema_backend.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
