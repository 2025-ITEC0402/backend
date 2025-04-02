package com.ema.ema_backend.domain.auth.service;

import com.ema.ema_backend.domain.auth.dto.KakaoTokenResponse;
import com.ema.ema_backend.domain.auth.dto.KakaoUserResponse;
import com.ema.ema_backend.domain.auth.properties.KakaoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoApiService {
    private static final String KAKAO_AUTH_BASE_URL = "https://kauth.kakao.com/oauth";
    private static final String KAKAO_API_BASE_URL = "https://kapi.kakao.com/v2/user";

    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;


    public KakaoTokenResponse getAccessToken(String authorizationCode) {
        log.info("카카오 인가코드: {}", authorizationCode);
        String url = KAKAO_AUTH_BASE_URL + "/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        String redirectUri = kakaoProperties.getRedirectUri();

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);

        RequestEntity<LinkedMultiValueMap<String, String>> request = new RequestEntity<>(body,
                headers, HttpMethod.POST, URI.create(url));

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(request,
                KakaoTokenResponse.class);

        log.info("카카오 accessToken: {}", response.getBody().getAccessToken());

        return response.getBody();
    }

    //TODO: GET요청으로 바꾸기
    public KakaoUserResponse getUserInfo(String accessToken) {
        String url = KAKAO_API_BASE_URL + "/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=utf-8"));
        headers.setBearerAuth(accessToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, request, KakaoUserResponse.class);

        log.info("카카오 사용자 정보 응답 JSON: {}", response.getBody());
        if (response.getBody().getKakaoAccount().getEmail() == null) {
            throw new RuntimeException("카카오 계정으로부터 전달받은 이메일이 없습니다.");
        }
        return response.getBody();
    }
}
