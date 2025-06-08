package com.ema.ema_backend.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public record GeminiResponse(List<Candidate> candidates) {

    public String extractText() {
        if (this.candidates != null && !this.candidates.isEmpty()) {
            Candidate firstCandidate = this.candidates.get(0);
            if (firstCandidate.content() != null && firstCandidate.content().parts() != null && !firstCandidate.content().parts().isEmpty()) {
                return firstCandidate.content().parts().get(0).text();
            }
        }
        return "응답 내용을 추출할 수 없습니다.";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Candidate(Content content) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Content(List<Part> parts, String role) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Part(String text){}
}
