package com.ema.ema_backend.domain.member.dto;

import java.time.LocalDate;

public record StreakSet(LocalDate date, Integer solvedCount) {
}
