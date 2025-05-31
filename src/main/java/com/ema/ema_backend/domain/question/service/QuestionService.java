package com.ema.ema_backend.domain.question.service;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.service.MemberService;
import com.ema.ema_backend.domain.question.dto.QuestionSet;
import com.ema.ema_backend.domain.question.dto.RecommendedQuestionInfoResponse;
import com.ema.ema_backend.domain.question.repository.QuestionRepository;
import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    public ResponseEntity<RecommendedQuestionInfoResponse> getRecommendQuestion(Authentication authentication) {
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new MemberNotFoundException("Member not found at QuestionService - getRecommendQuestion()");
        }
        Member member = optionalMember.get();

        List<QuestionSet> questionSets = new ArrayList<>();

        ChapterType chapterType1 = member.getLearningHistory().getRecommendedChapter1();
        QuestionSet qs1 = new QuestionSet(chapterType1.getChapterName(), chapterType1.toString(), "EASY");
        questionSets.add(qs1);

        ChapterType chapterType2 = member.getLearningHistory().getRecommendedChapter2();
        QuestionSet qs2 = new QuestionSet(chapterType2.getChapterName(), chapterType2.toString(), "NORMAL");
        questionSets.add(qs2);

        ChapterType chapterType3 = member.getLearningHistory().getRecommendedChapter3();
        QuestionSet qs3 = new QuestionSet(chapterType3.getChapterName(), chapterType3.toString(), "HARD");
        questionSets.add(qs3);

        RecommendedQuestionInfoResponse response = new RecommendedQuestionInfoResponse(questionSets);
        return ResponseEntity.ok(response);
    }
}
