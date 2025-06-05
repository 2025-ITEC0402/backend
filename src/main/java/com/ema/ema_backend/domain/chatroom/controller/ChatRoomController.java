package com.ema.ema_backend.domain.chatroom.controller;

import com.ema.ema_backend.domain.chatroom.dto.FirstChatRequest;
import com.ema.ema_backend.domain.chatroom.dto.FirstChatResponse;
import com.ema.ema_backend.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/new-chat")
    public ResponseEntity<FirstChatResponse> postNewChat(@RequestBody FirstChatRequest req, Authentication authentication){
        return chatRoomService.postNewChat(req, authentication);
    }
}
