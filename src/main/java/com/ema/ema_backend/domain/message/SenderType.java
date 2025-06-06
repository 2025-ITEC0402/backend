package com.ema.ema_backend.domain.message;

import lombok.Getter;

@Getter
public enum SenderType {
    USER("사용자"), AI("공학수학 어시스턴스"), INVALID("잘못된 값");

    private final String korType;

    SenderType(String korType) {
        this.korType = korType;
    }

    public static SenderType getSenderType(String role) {
        for (SenderType senderType : SenderType.values()) {
            if (senderType.getKorType().equals(role)) {
                return senderType;
            }
        }
        return INVALID;
    }
}
