package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.repository.MemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final MemberRepository memberRepository;

    public Optional<Member> checkPermission(Authentication authentication){
        return memberRepository.findByEmail(authentication.getName());
    }
}
