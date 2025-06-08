package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.member.dto.GeminiRequest;
import com.ema.ema_backend.domain.member.dto.GeminiResponse;
import com.ema.ema_backend.domain.member.dto.LearningRecommendation;
import com.ema.ema_backend.global.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String getGeminiResponse(String prompt) {
        String requestUrl = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequest requestBody = GeminiRequest.fromPrompt(prompt);

        HttpEntity<GeminiRequest> request = new HttpEntity<>(requestBody, headers);

        String result = null;
        try {
            GeminiResponse response = restTemplate.postForObject(requestUrl, request, GeminiResponse.class);
            if (response == null) {
                throw new ExternalApiException("Gemini API 응답이 없습니다.");
            }
            return response.extractText();
        } catch (Exception e) {
            throw new ExternalApiException("Gemini API 호출 중 오류 발생");
        }
    }

}
