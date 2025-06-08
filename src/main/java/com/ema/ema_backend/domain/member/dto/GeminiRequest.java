package com.ema.ema_backend.domain.member.dto;

import java.util.Collections;
import java.util.List;

public record GeminiRequest(List<Content> contents) {

    public static GeminiRequest fromPrompt(String prompt) {
        Part part = new Part(prompt);
        Content content = new Content(Collections.singletonList(part));
        return new GeminiRequest(Collections.singletonList(content));
    }

    public static record Content(List<Part> parts) {}
    public static record Part(String text) {}
}
