package com.ema.ema_backend.domain.chatroom.controller;

import com.ema.ema_backend.domain.chatroom.dto.ChatRequest;
import com.ema.ema_backend.domain.chatroom.dto.ChatResponse;
import com.ema.ema_backend.domain.chatroom.dto.FirstChatResponse;
import com.ema.ema_backend.domain.chatroom.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @Operation(
            summary = "새 채팅방 생성 및 첫 메시지 전송",
            description = "인증된 사용자가 새로운 채팅방을 생성하고, 첫 번째 메시지를 서버로 전송합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채팅방 생성 및 첫 메시지 전송 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FirstChatResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청(유효하지 않은 파라미터 등)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음(유효한 JWT 토큰 필요)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content
                    )
            }
    )
    @PostMapping("/new-chat")
    public ResponseEntity<FirstChatResponse> postNewChat(@RequestBody ChatRequest req, Authentication authentication){
        return chatRoomService.postNewChat(req, authentication);
    }

    @Operation(
            summary = "채팅방에 메시지 전송",
            description = "인증된 사용자가 지정된 채팅방에 새로운 메시지를 전송합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "메시지 전송 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChatResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청(유효하지 않은 파라미터 등)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음(유효한 JWT 토큰 필요)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 채팅방을 찾을 수 없음",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content
                    )
            }
    )
    @PostMapping("/{chatRoomId}")
    public ResponseEntity<ChatResponse> postChat(@PathVariable Long chatRoomId, @RequestBody ChatRequest req, Authentication authentication){
        return chatRoomService.postChat(chatRoomId, req, authentication);
    }
}
