package com.ema.ema_backend.domain.memberquestion.service;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import com.ema.ema_backend.domain.memberquestion.repository.MemberQuestionRepository;
import com.ema.ema_backend.domain.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQuestionService {
    private final MemberQuestionRepository memberQuestionRepository;

    public void createMemberQuestion(Member member, Question question) {
        MemberQuestion memberQuestion = memberQuestionRepository.save(new MemberQuestion(member, question));
        member.getMemberQuestions().add(memberQuestion);
        question.getQuestionList().add(memberQuestion);
    }

    public void deleteMemberQuestion(MemberQuestion mq) {
        memberQuestionRepository.delete(mq);
    }
}
