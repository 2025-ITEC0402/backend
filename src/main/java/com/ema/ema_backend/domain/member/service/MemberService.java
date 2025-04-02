package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.auth.dto.KakaoTokenResponse;
import com.ema.ema_backend.domain.auth.dto.KakaoUserResponse;
import com.ema.ema_backend.domain.auth.dto.TokenResponse;
import com.ema.ema_backend.domain.auth.service.KakaoApiService;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.repository.MemberRepository;
import com.ema.ema_backend.domain.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final KakaoApiService kakaoApiService;
    private final JwtProvider jwtProvider;

    public TokenResponse kakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = kakaoApiService.getAccessToken(code);
        KakaoUserResponse userResponse = kakaoApiService.getUserInfo(tokenResponse.getAccessToken());

        String email = userResponse.getKakaoAccount().getEmail();

        //TODO: 토큰 저장 kakao 전용 테이블을 만들어야할듯?

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isEmpty()) {
            registerNewMember(userResponse.getKakaoAccount().getEmail(), userResponse.getKakaoAccount().getProfile().getNickname());
        }

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return new TokenResponse(accessToken, refreshToken);

    }

    private void registerNewMember(String email, String nickname) {
        if (memberRepository.findByEmail(email).isPresent()) {
            //TODO: 예외처리 다시 할것 커스텀 예외로
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        Member member = Member.create(nickname, email);
        memberRepository.save(member);
    }
}
