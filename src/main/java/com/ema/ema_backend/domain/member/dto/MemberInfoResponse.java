package com.ema.ema_backend.domain.member.dto;

public record MemberInfoResponse(String name, Integer todaySolved, Integer allTimeSolved, Integer streakDays) {
}
