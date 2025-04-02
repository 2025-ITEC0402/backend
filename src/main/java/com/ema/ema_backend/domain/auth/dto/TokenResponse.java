package com.ema.ema_backend.domain.auth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
