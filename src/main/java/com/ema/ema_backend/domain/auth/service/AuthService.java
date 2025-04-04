package com.ema.ema_backend.domain.auth.service;

import com.ema.ema_backend.domain.auth.dto.KakaoTokenResponse;
import com.ema.ema_backend.domain.auth.dto.KakaoUserResponse;
import com.ema.ema_backend.domain.auth.dto.TokenDto;
import com.ema.ema_backend.domain.auth.jwt.JwtProvider;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.repository.MemberRepository;
import com.ema.ema_backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final KakaoApiService kakaoApiService;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenDto kakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = kakaoApiService.getAccessToken(code);
        KakaoUserResponse userResponse = kakaoApiService.getUserInfo(tokenResponse.getAccessToken());

        String email = userResponse.getKakaoAccount().getEmail();
        String nickname = userResponse.getKakaoAccount().getProfile().getNickname();

        log.info("카카오 로그인 요청 email: {}, nickname: {}", email, nickname);

        //TODO: kakao 토큰 저장 전용 테이블을 만들어야할듯?

        registerMemberIfNotExist(nickname, email);

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return new TokenDto(accessToken, refreshToken);

    }


    private void registerMemberIfNotExist(String nickname, String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isEmpty()) {
            memberService.registerNewMember(nickname, email);
        }
    }

}
