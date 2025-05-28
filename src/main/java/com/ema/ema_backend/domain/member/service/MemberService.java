package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.member.entity.LearningHistory;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.repository.LearningHistoryRepository;
import com.ema.ema_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final LearningHistoryRepository learningHistoryRepository;

    public void registerNewMember(String nickname, String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            //TODO: 예외처리 다시 할것 커스텀 예외로
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        LearningHistory learningHistory = new LearningHistory();
        learningHistoryRepository.save(learningHistory);

        Member member = Member.createByNameAndEmail(nickname, email, learningHistory);
        memberRepository.save(member);
    }

    public Optional<Member> checkPermission(Authentication authentication){
        return memberRepository.findByEmail(authentication.getName());
    }
}
