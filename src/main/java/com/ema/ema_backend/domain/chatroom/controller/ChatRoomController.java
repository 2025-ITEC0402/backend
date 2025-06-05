package com.ema.ema_backend.domain.chatroom.controller;

import com.ema.ema_backend.domain.chatroom.dto.ChatRequest;
import com.ema.ema_backend.domain.chatroom.dto.ChatResponse;
import com.ema.ema_backend.domain.chatroom.dto.FirstChatResponse;
import com.ema.ema_backend.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/new-chat")
    public ResponseEntity<FirstChatResponse> postNewChat(@RequestBody ChatRequest req, Authentication authentication){
        return chatRoomService.postNewChat(req, authentication);
    }

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<ChatResponse> postChat(@PathVariable Long chatRoomId, @RequestBody ChatRequest req, Authentication authentication){
        return chatRoomService.postChat(chatRoomId, req, authentication);
    }
}
