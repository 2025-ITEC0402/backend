package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.member.dto.LearningRecommendation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 추가합니다.
public class JsonParsingService {

    // Spring 컨테이너가 관리하는 ObjectMapper Bean을 주입받습니다.
    private final ObjectMapper objectMapper;

    public LearningRecommendation parse(String jsonString) {
        try {
            // objectMapper.readValue() 메소드로 JSON 문자열을 DTO 객체로 변환합니다.
            return objectMapper.readValue(jsonString, LearningRecommendation.class);
        } catch (JsonProcessingException e) {
            // JSON 형식이 잘못되었을 경우 예외가 발생합니다.
            // 실제 코드에서는 로깅 및 예외 처리가 필요합니다.
            throw new RuntimeException("JSON 파싱 중 에러가 발생했습니다.", e);
        }
    }
}