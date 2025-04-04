package com.ema.ema_backend.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KakaoUserResponse {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    // 내부 클래스
    @Getter
    @Setter
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @Getter
    @Setter
    public static class Profile {
        private String nickname;
    }
}
