package com.ema.ema_backend.domain.member.controller;

import com.ema.ema_backend.domain.member.dto.MemberInfoResponse;
import com.ema.ema_backend.domain.member.dto.StreakInfoResponse;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check")
    public ResponseEntity<String> checkPermission(Authentication authentication) {
        return memberService.testCheckPermission(authentication);
    }

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(Authentication authentication) {
        return memberService.getMemberInfo(authentication);
    }

    @GetMapping("/streak")
    public ResponseEntity<StreakInfoResponse> getStreakInfo(Authentication authentication) {
        return memberService.getStreakInfo(authentication);
    }

    @PutMapping("/learning-history")
    public ResponseEntity<Void> updateLearningHistory(Authentication authentication) {
        return memberService.updateLearningHistory(authentication);
    }
}
