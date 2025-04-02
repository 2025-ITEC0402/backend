package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.auth.service.KakaoApiService;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.repository.MemberRepository;
import com.ema.ema_backend.domain.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public void registerNewMember(String nickname, String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            //TODO: 예외처리 다시 할것 커스텀 예외로
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        Member member = Member.createByNameAndEmail(nickname, email);
        memberRepository.save(member);
    }
}
