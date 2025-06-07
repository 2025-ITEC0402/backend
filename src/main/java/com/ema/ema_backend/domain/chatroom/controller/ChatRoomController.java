package com.ema.ema_backend.domain.chatroom.controller;

import com.ema.ema_backend.domain.chatroom.dto.*;
import com.ema.ema_backend.domain.chatroom.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

//    @Operation(
//            summary = "채팅방에 메시지 전송",
//            description = "인증된 사용자가 지정된 채팅방에 새로운 메시지를 전송합니다.",
//            responses = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "메시지 전송 성공",
//                            content = @Content(
//                                    mediaType = "application/json",
//                                    schema = @Schema(implementation = ChatResponse.class)
//                            )
//                    ),
//                    @ApiResponse(
//                            responseCode = "400",
//                            description = "잘못된 요청(유효하지 않은 파라미터 등)",
//                            content = @Content
//                    ),
//                    @ApiResponse(
//                            responseCode = "401",
//                            description = "인증되지 않음(유효한 JWT 토큰 필요)",
//                            content = @Content
//                    ),
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "해당 채팅방을 찾을 수 없음",
//                            content = @Content
//                    ),
//                    @ApiResponse(
//                            responseCode = "500",
//                            description = "서버 내부 오류",
//                            content = @Content
//                    )
//            }
//    )
//    @PostMapping("/{chatRoomId}")
//    public ResponseEntity<ChatResponse> postChat(@PathVariable Long chatRoomId, @RequestBody ChatRequest req, Authentication authentication){
//        return chatRoomService.postChat(chatRoomId, req, authentication);
//    }

    @Operation(
            summary = "채팅방에 이미지/텍스트 메시지 전송",
            description = """
    multipart/form-data 로 이미지 파일(img)과  
    텍스트 쿼리(query)를 함께 업로드합니다.  
    • img만 보낼 때: img 파트만 포함  
    • query만 보낼 때: query 파트만 포함  
    • 둘 다 보낼 때: img + query 둘 다 포함  
    """
    )
    @PostMapping(
            value    = "/{chatRoomId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChatResponse> postChatImg(
            @PathVariable Long chatRoomId,

            @Parameter(
                    description = "이미지 파일 (선택)",
                    required    = false,
                    content     = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema    = @Schema(type="string", format="binary")
                    )
            )
            @RequestPart(value = "img", required = false)
            MultipartFile file,

            @Parameter(
                    description = "사용자 쿼리 텍스트 (선택)",
                    required    = false
            )
            @RequestPart(value = "query", required = false)
            String query,

            @Parameter(hidden = true)
            Authentication authentication
    ) {
        boolean hasImage = file != null && !file.isEmpty();
        boolean hasText  = StringUtils.hasText(query);

        if (!hasImage && !hasText) {
            throw new RuntimeException("이미지와 텍스트중 하나는 있어야합니다.");
        }

        ChatResponse result;
        if (hasImage && hasText) {
            // 이미지 + 텍스트 둘 다
            return chatRoomService.postChatImg(chatRoomId, file, query, authentication);
        } else if (hasImage) {
            // 이미지만
            return chatRoomService.postChatImg(chatRoomId, file, " ", authentication);
        } else {
            // 텍스트만
            return chatRoomService.postChat(chatRoomId, query, authentication);
        }
    }


    @Operation(
            summary = "채팅방 상세 조회",
            description = "인증된 사용자가 특정 채팅방의 세부 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채팅방 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChatRoomResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음(유효한 JWT 토큰 필요)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음(해당 채팅방에 대한 조회 권한이 없음)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 ID의 채팅방을 찾을 수 없음",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(@PathVariable Long chatRoomId, Authentication authentication){
        return chatRoomService.getChatRoom(chatRoomId, authentication);
    }

    @Operation(
            summary = "채팅방 제목 수정",
            description = "인증된 사용자가 특정 채팅방의 제목을 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채팅방 제목 수정 성공",
                            content = @Content
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
                            responseCode = "403",
                            description = "권한 없음(해당 채팅방의 제목을 수정할 권한이 없음)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 ID의 채팅방을 찾을 수 없음",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{chatRoomId}")
    public ResponseEntity<Void> updateChatRoomTitle(@PathVariable Long chatRoomId, @RequestBody TitleUpdateRequest req, Authentication authentication){
        return chatRoomService.updateChatRoomTitle(chatRoomId, req, authentication);
    }

    @Operation(
            summary = "채팅방 삭제",
            description = "인증된 사용자가 특정 채팅방을 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채팅방 삭제 성공",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않음(유효한 JWT 토큰 필요)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음(해당 채팅방 삭제 권한이 없음)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 ID의 채팅방을 찾을 수 없음",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomId, Authentication authentication){
        return chatRoomService.deleteChatRoom(chatRoomId, authentication);
    }

    @Operation(
            summary = "모든 채팅방 정보 조회",
            description = "인증된 사용자가 참여 중인 모든 채팅방의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "채팅방 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChatRoomInfoResponse.class)
                            )
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
    @GetMapping
    public ResponseEntity<ChatRoomInfoResponse> getChatRoomInfos(Authentication authentication){
        return chatRoomService.getChatRoomInfos(authentication);
    }
}
