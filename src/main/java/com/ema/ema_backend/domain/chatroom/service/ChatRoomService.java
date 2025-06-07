package com.ema.ema_backend.domain.chatroom.service;

import com.ema.ema_backend.domain.chatroom.ChatRoom;
import com.ema.ema_backend.domain.chatroom.dto.*;
import com.ema.ema_backend.domain.chatroom.repository.ChatRoomRepository;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.service.MemberService;
import com.ema.ema_backend.domain.message.Message;
import com.ema.ema_backend.domain.message.service.MessageService;
import com.ema.ema_backend.global.exception.NotFoundException;
import com.ema.ema_backend.global.exception.RemoteApiException;
import com.ema.ema_backend.global.exception.UnauthorizedAccessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberService memberService;
    private final MessageService messageService;
    private final RestTemplate restTemplate;
    private final ImageUploadService imageUploadService;

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

        Message userMessage = messageService.createTextMessage("사용자", req.content(), chatRoom);
        chatRoom.getMessages().add(userMessage);

        // RestTemplate 통해 파이썬 서버 연결
        PyPostChatFirstResponse response = postFirstChat(new PyPostChatRequest(req.content()));

        Message aiMessage = messageService.createTextMessage("공학수학 어시스턴스", response.answer(), chatRoom);
        chatRoom.getMessages().add(aiMessage);

        chatRoom.setRoomTitle(response.title());

        // FirstChatResponse 데이터 조립
        return new ResponseEntity<>(new FirstChatResponse(chatRoom.getId(), response.title(), aiMessage.getSenderType().toString(), response.answer(), aiMessage.getCreatedAt()), HttpStatus.OK);
    }


    @Transactional
    public PyPostChatFirstResponse postFirstChat(PyPostChatRequest req){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity에 바디와 헤더 담기
        HttpEntity<PyPostChatRequest> httpEntity = new HttpEntity<>(req, headers);

        // RestTemplate으로 POST 요청 보내기
        ResponseEntity<PyPostChatFirstResponse> responseEntity = restTemplate.postForEntity(
                baseUri + "/qnantitle",
                httpEntity,
                PyPostChatFirstResponse.class
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
            throw new NotFoundException("ChatRoom", "at ChatRoomService - postChat()");
        }
        ChatRoom chatRoom = optionalChatRoom.get();

        // 유저의 chatroom이 맞는지 검증
        if (!chatRoom.getMember().equals(member)){
            throw new UnauthorizedAccessException("ChatRoom", "at ChatRoomService - postChat()");
        }

        Message userMessage = messageService.createTextMessage("사용자", req.content(), chatRoom);
        chatRoom.getMessages().add(userMessage);

        // RestTemplate 통해 파이썬 서버 연결
        PyPostChatResponse response = postChat(new PyPostChatRequest(req.content()));

        Message aiMessage = messageService.createTextMessage("공학수학 어시스턴스", response.answer(), chatRoom);
        chatRoom.getMessages().add(aiMessage);

        // FirstChatResponse 데이터 조립
        return new ResponseEntity<>(new ChatResponse(chatRoom.getId(), aiMessage.getSenderType().toString(), response.answer(), aiMessage.getCreatedAt()), HttpStatus.OK);
    }

    @Transactional
    public PyPostChatResponse postChat(PyPostChatRequest req){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity에 바디와 헤더 담기
        HttpEntity<PyPostChatRequest> httpEntity = new HttpEntity<>(req, headers);

        // RestTemplate으로 POST 요청 보내기
        ResponseEntity<PyPostChatResponse> responseEntity = restTemplate.postForEntity(
                baseUri + "/qna",
                httpEntity,
                PyPostChatResponse.class
        );

        // HTTP 상태 코드 체크 (200 OK 등)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // 8) 바디에 담긴 QuestionResponse 객체 꺼내기
            return responseEntity.getBody();
        } else {
            // 오류 응답 처리 (예외 던지기 등)
            throw new RemoteApiException("원격 서버 응답 실패: HTTP " + responseEntity.getStatusCode() + "," + baseUri + "/qua");
        }
    }

    @Transactional
    public ResponseEntity<ChatRoomResponse> getChatRoom(Long chatRoomId, Authentication authentication){
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()){
            throw new NotFoundException("Member", "at ChatRoomService - getChatRoom()");
        }
        Member member = optionalMember.get();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if (optionalChatRoom.isEmpty()){
            throw new NotFoundException("ChatRoom", "at ChatRoomService - getChatRoom()");
        }
        ChatRoom chatRoom = optionalChatRoom.get();

        if (!chatRoom.getMember().equals(member)){
            throw new UnauthorizedAccessException("ChatRoom", "at ChatRoomService - getChatRoom()");
        }

        if (chatRoom.getMessages().isEmpty()){
            return new ResponseEntity<>(new ChatRoomResponse(chatRoom.getId(), chatRoom.getRoomTitle(), new ArrayList<>()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getRoomTitle(),
                messageService.getMessageSet(
                        chatRoom.getMessages()
                                .stream()
                                .map(Message::getId)
                                .collect(Collectors.toList()))), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> updateChatRoomTitle(Long chatRoomId, TitleUpdateRequest req, Authentication authentication){
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()){
            throw new NotFoundException("Member", "at ChatRoomService - updateChatRoomTitle()");
        }
        Member member = optionalMember.get();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if (optionalChatRoom.isEmpty()){
            throw new NotFoundException("ChatRoom", "at ChatRoomService - updateChatRoomTitle()");
        }
        ChatRoom chatRoom = optionalChatRoom.get();

        if (!chatRoom.getMember().equals(member)){
            throw new UnauthorizedAccessException("ChatRoom", "at ChatRoomService - updateChatRoomTitle()");
        }
        chatRoom.setRoomTitle(req.newTitle());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> deleteChatRoom(Long chatRoomId, Authentication authentication){
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()){
            throw new NotFoundException("Member", "at ChatRoomService - deleteChatRoom()");
        }
        Member member = optionalMember.get();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if (optionalChatRoom.isEmpty()){
            throw new NotFoundException("ChatRoom", "at ChatRoomService - deleteChatRoom()");
        }
        ChatRoom chatRoom = optionalChatRoom.get();

        if (!chatRoom.getMember().equals(member)){
            throw new UnauthorizedAccessException("ChatRoom", "at ChatRoomService - deleteChatRoom()");
        }

        // 채팅방에 있는 message 모두 삭제
        // @OneToMany(
        //        mappedBy = "chatRoom",
        //        cascade = CascadeType.ALL,
        //        orphanRemoval = true
        //    )
        // 태그로 인해 message 들 자동으로 삭제됨.
        chatRoomRepository.deleteById(chatRoomId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<ChatRoomInfoResponse> getChatRoomInfos(Authentication authentication){
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()){
            throw new NotFoundException("Member", "at ChatRoomService - getChatRoomInfos()");
        }
        Member member = optionalMember.get();

        List<ChatRoomWithoutMessagesResponse> responseList = new ArrayList<>();
        for (ChatRoom chatRoom : member.getChatRooms()){
            responseList.add(new ChatRoomWithoutMessagesResponse(chatRoom.getId(), chatRoom.getRoomTitle()));
        }

        return new ResponseEntity<>(new ChatRoomInfoResponse(responseList), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<ChatResponse> postChatImg(Long ChatRoomId, MultipartFile file, String req, Authentication authentication) {
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()){
            throw new NotFoundException("Member", "at ChatRoomService - postChat()");
        }
        Member member = optionalMember.get();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(ChatRoomId);
        if (optionalChatRoom.isEmpty()){
            throw new NotFoundException("ChatRoom", "at ChatRoomService - postChat()");
        }
        ChatRoom chatRoom = optionalChatRoom.get();

        // 유저의 chatroom이 맞는지 검증
        if (!chatRoom.getMember().equals(member)){
            throw new UnauthorizedAccessException("ChatRoom", "at ChatRoomService - postChat()");
        }

        // Base64 인코더로 인코딩
        String dataUri = null;
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            dataUri = "data:" + file.getContentType() + ";base64," + base64;
        } catch (IOException e) {
            throw new RuntimeException("이미지 I/O Exception", e);
        }

        String imageUrl = imageUploadService.uploadAndSave(ChatRoomId, file);
        Message userMessage = messageService.createImgMessage("사용자", imageUrl, req, chatRoom);
        chatRoom.getMessages().add(userMessage);

        // RestTemplate 통해 파이썬 서버 연결
        PyPostChatResponse response = postChatImg(new PyPostChatImgRequest(req, dataUri));

        Message aiMessage = messageService.createTextMessage("공학수학 어시스턴스", response.answer(), chatRoom);
        chatRoom.getMessages().add(aiMessage);

        // FirstChatResponse 데이터 조립
        return new ResponseEntity<>(new ChatResponse(chatRoom.getId(), aiMessage.getSenderType().toString(), response.answer(), aiMessage.getCreatedAt()), HttpStatus.OK);
    }

    @Transactional
    public PyPostChatResponse postChatImg(PyPostChatImgRequest req){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity에 바디와 헤더 담기
        HttpEntity<PyPostChatImgRequest> httpEntity = new HttpEntity<>(req, headers);

        // RestTemplate으로 POST 요청 보내기
        ResponseEntity<PyPostChatResponse> responseEntity = restTemplate.postForEntity(
                baseUri + "/qnaimg",
                httpEntity,
                PyPostChatResponse.class
        );

        // HTTP 상태 코드 체크 (200 OK 등)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // 8) 바디에 담긴 QuestionResponse 객체 꺼내기
            return responseEntity.getBody();
        } else {
            // 오류 응답 처리 (예외 던지기 등)
            throw new RemoteApiException("원격 서버 응답 실패: HTTP " + responseEntity.getStatusCode() + "," + baseUri + "/qua");
        }
    }
}
