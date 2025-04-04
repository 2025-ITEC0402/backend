package com.ema.ema_backend.domain.auth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
