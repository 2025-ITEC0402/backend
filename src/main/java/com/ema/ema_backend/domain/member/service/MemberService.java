package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.member.dto.MemberInfoResponse;
import com.ema.ema_backend.domain.member.entity.LearningHistory;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.repository.LearningHistoryRepository;
import com.ema.ema_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private Optional<Member> checkPermission(Authentication authentication){
        return memberRepository.findByEmail(authentication.getName());
    }

    public ResponseEntity<String> testCheckPermission(Authentication authentication){
        Optional<Member> optionalMember = checkPermission(authentication);
        if (optionalMember.isPresent()) {
            return ResponseEntity.ok(optionalMember.get().getEmail());
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<MemberInfoResponse> getMemberInfo(Authentication authentication) {
        Optional<Member> optionalMember = checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Member member = optionalMember.get();

        // 오늘 푼 문제 체크
        Integer todaySolved = 0;

        // 전체 푼 문제 체크
        Integer allTimeSolved = 0;

        return new ResponseEntity<>(new MemberInfoResponse(member.getName(), todaySolved, allTimeSolved), HttpStatus.OK);
    }
}
