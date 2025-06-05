package com.ema.ema_backend.domain.message;

import lombok.Getter;

@Getter
public enum SenderType {
    USER("사용자"), SERVER("서버"), INVALID("잘못된 값");

    private String value;

    SenderType(String value) {
        this.value = value;
    }

    public static SenderType getSenderType(String role) {
        for (SenderType senderType : SenderType.values()) {
            if (senderType.value.equals(role)) {
                return senderType;
            }
        }
        return INVALID;
    }
}
