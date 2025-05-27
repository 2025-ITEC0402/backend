package com.ema.ema_backend.domain.member.controller;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isPresent()) {
            return ResponseEntity.ok(optionalMember.get().getEmail());
        }
        return ResponseEntity.notFound().build();
    }

}
