package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.member.dto.MemberInfoResponse;
import com.ema.ema_backend.domain.member.dto.StreakInfoResponse;
import com.ema.ema_backend.domain.member.dto.StreakSet;
import com.ema.ema_backend.domain.member.entity.LearningHistory;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.repository.LearningHistoryRepository;
import com.ema.ema_backend.domain.member.repository.MemberRepository;
import com.ema.ema_backend.domain.memberquestion.service.MemberQuestionService;
import com.ema.ema_backend.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final LearningHistoryRepository learningHistoryRepository;
    private final MemberQuestionService memberQuestionService;

    public void registerNewMember(String nickname, String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            //TODO: 예외처리 다시 할것 커스텀 예외로
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        LearningHistory learningHistory = new LearningHistory();
        learningHistoryRepository.save(learningHistory);

        Member member = Member.createByNameAndEmail(nickname, email, learningHistory);
        member.getLearningHistory().setMember(member);
        memberRepository.save(member);
    }

    public Optional<Member> checkPermission(Authentication authentication){
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

        return new ResponseEntity<>(memberQuestionService.getMemberInfo(member), HttpStatus.OK);
    }

    public ResponseEntity<StreakInfoResponse> getStreakInfo(Authentication authentication) {
        Optional<Member> optionalMember = checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", " ");
        }
        Member member = optionalMember.get();

        return new ResponseEntity<>(new StreakInfoResponse(
             memberQuestionService.getStreakInfo(member)
        ), HttpStatus.OK);
    }

    public ResponseEntity<Void> updateLearningHistory(Authentication authentication){
        Optional<Member> optionalMember = checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", " ");
        }
        Member member = optionalMember.get();

        // RestTemplate 으로 LLM 호출 후 학습 이력 갱신하기


        // 결과 json 파싱

        String chapter1 = "3";
        String chapter2 = "6";
        String chapter3 = "7";
        String goal = "변경된 목표입니다.";

        member.getLearningHistory().update(chapter1, chapter2, chapter3, goal, member);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
