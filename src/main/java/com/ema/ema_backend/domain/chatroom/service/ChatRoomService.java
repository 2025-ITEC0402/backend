package com.ema.ema_backend.domain.chatroom.service;

import com.ema.ema_backend.domain.chatroom.ChatRoom;
import com.ema.ema_backend.domain.chatroom.dto.*;
import com.ema.ema_backend.domain.chatroom.repository.ChatRoomRepository;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.service.MemberService;
import com.ema.ema_backend.domain.message.Message;
import com.ema.ema_backend.domain.message.repository.MessageRepository;
import com.ema.ema_backend.global.exception.NotFoundException;
import com.ema.ema_backend.global.exception.RemoteApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberService memberService;
    private final MessageRepository messageRepository;
    private final RestTemplate restTemplate;

    @Value("${PY_SERVER_BASE_URI}")
    private String baseUri;

    @Transactional
    public ResponseEntity<FirstChatResponse> postNewChat(ChatRequest req, Authentication authentication){
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if(optionalMember.isEmpty()){
            throw new NotFoundException("Member", "at ChatRoomService - postNewChat()");
        }
        Member member = optionalMember.get();


        ChatRoom chatRoom = new ChatRoom("Initial Chat", member);
        chatRoomRepository.save(chatRoom);

        Message userMessage = new Message("사용자", req.content(), chatRoom);
        messageRepository.save(userMessage);
        chatRoom.getMessages().add(userMessage);

        // RestTemplate 통해 파이썬 서버 연결
        PyPostNewChatFirstResponse response = postFirstChat(new PyPostNewChatRequest(req.content()));

        Message aiMessage = new Message("서버", response.answer(), chatRoom);
        messageRepository.save(aiMessage);
        chatRoom.getMessages().add(aiMessage);

        chatRoom.setRoomTitle(response.title());

        // FirstChatResponse 데이터 조립
        return new ResponseEntity<>(new FirstChatResponse(chatRoom.getId(), response.title(), "서버", response.answer(), aiMessage.getCreatedAt()), HttpStatus.OK);
    }


    @Transactional
    public PyPostNewChatFirstResponse postFirstChat(PyPostNewChatRequest req){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity에 바디와 헤더 담기
        HttpEntity<PyPostNewChatRequest> httpEntity = new HttpEntity<>(req, headers);

        // RestTemplate으로 POST 요청 보내기
        ResponseEntity<PyPostNewChatFirstResponse> responseEntity = restTemplate.postForEntity(
                baseUri + "/qnantitle",
                httpEntity,
                PyPostNewChatFirstResponse.class
        );

        // HTTP 상태 코드 체크 (200 OK 등)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // 8) 바디에 담긴 QuestionResponse 객체 꺼내기
            return responseEntity.getBody();
        } else {
            // 오류 응답 처리 (예외 던지기 등)
            throw new RemoteApiException("원격 서버 응답 실패: HTTP " + responseEntity.getStatusCode() + "," + baseUri + "/quantitle");
        }
    }

    @Transactional
    public ResponseEntity<ChatResponse> postChat(Long ChatRoomId, ChatRequest req, Authentication authentication){
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()){
            throw new NotFoundException("Member", "at ChatRoomService - postChat()");
        }
        Member member = optionalMember.get();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(ChatRoomId);
        if (optionalChatRoom.isEmpty()){

        }

    }
}
