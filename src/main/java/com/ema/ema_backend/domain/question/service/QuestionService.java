package com.ema.ema_backend.domain.question.service;

import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.service.MemberService;
import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import com.ema.ema_backend.domain.memberquestion.service.MemberQuestionService;
import com.ema.ema_backend.domain.question.Question;
import com.ema.ema_backend.domain.question.dto.QuestionSet;
import com.ema.ema_backend.domain.question.dto.QuestionsInfoResponse;
import com.ema.ema_backend.domain.question.dto.RecommendSet;
import com.ema.ema_backend.domain.question.dto.RecommendedQuestionInfoResponse;
import com.ema.ema_backend.domain.question.repository.QuestionRepository;
import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.domain.type.DifficultyType;
import com.ema.ema_backend.global.exception.MemberNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final MemberQuestionService memberQuestionService;

    public ResponseEntity<RecommendedQuestionInfoResponse> getRecommendQuestion(Authentication authentication) {
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new MemberNotFoundException("Member not found at QuestionService - getRecommendQuestion()");
        }
        Member member = optionalMember.get();

        List<RecommendSet> recommendSets = new ArrayList<>();

        ChapterType chapterType1 = member.getLearningHistory().getRecommendedChapter1();
        RecommendSet qs1 = new RecommendSet(chapterType1.getChapterName(), chapterType1.toString(), "EASY");
        recommendSets.add(qs1);

        ChapterType chapterType2 = member.getLearningHistory().getRecommendedChapter2();
        RecommendSet qs2 = new RecommendSet(chapterType2.getChapterName(), chapterType2.toString(), "NORMAL");
        recommendSets.add(qs2);

        ChapterType chapterType3 = member.getLearningHistory().getRecommendedChapter3();
        RecommendSet qs3 = new RecommendSet(chapterType3.getChapterName(), chapterType3.toString(), "HARD");
        recommendSets.add(qs3);

        RecommendedQuestionInfoResponse response = new RecommendedQuestionInfoResponse(recommendSets);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<QuestionsInfoResponse> get3RandomQuestions(Authentication authentication) {
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new MemberNotFoundException("Member not found at QuestionService - getQuestionsInfo()");
        }
        Member member = optionalMember.get();

        List<Question> questionList = questionRepository.find3RandomQuestionsNotSolvedByMember(member.getId());

        List<QuestionSet> questions = new ArrayList<>();

        for (Question q : questionList) {
            memberQuestionService.createMemberQuestion(member, q);
            questions.add(
                    new QuestionSet(
                            q.getId(),
                            q.getTitle(),
                            q.getChoice1(),
                            q.getChoice2(),
                            q.getChoice3(),
                            q.getChoice4(),
                            q.getAnswer(),
                            q.getExplanation(),
                            q.getDifficulty().toString(),
                            q.getChapter().toString()));
        }
        return ResponseEntity.ok(new QuestionsInfoResponse(questions));
    }

    @Transactional
    public ResponseEntity<Void> deleteMyQuestions(Authentication authentication) {
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new MemberNotFoundException("Member not found at QuestionService - getMyQuestions()");
        }
        Member member = optionalMember.get();

        List<MemberQuestion> toRemove = new ArrayList<>(member.getMemberQuestions());
        for (MemberQuestion mq : toRemove) {
            mq.removeFromBothSides();
            memberQuestionService.deleteMemberQuestion(mq);
        }
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<QuestionSet> generatePersonalizedQuestion(Authentication authentication) {
        Optional<Member> optionalMember = memberService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new MemberNotFoundException("Member not found at QuestionService - getPersonalizedQuestions()");
        }
        Member member = optionalMember.get();

        // 학습 이력 정제하기
        // 사용자가 풀이한 이력 + 사용자의 선호 난이도 정도 제공?


        // RestTemplate 로 파이썬 서버 API 이용하고 결과 받아오기


        // 결과를 Question 에 저장

        String title = "Agent가 생성한 문제입니다. 3 + 3 을 계산한 값은?";
        String choice1 = "4";
        String choice2 = "5";
        String choice3 = "6";
        String choice4 = "7";
        String answer = "3";
        String explanation = "왼손 손가락 3개를 펴고, 오른손 손가락 3개를 펴세요. 왼손부터 차례대로 세면 총 6개입니다. 따라서 답은 6입니다.";
        DifficultyType difficultyType = DifficultyType.VERY_EASY;
        ChapterType chapterType = ChapterType.CHAPTER_4;

        // Member와 매핑 (MemberQuestion에도 추가)
        Question question = Question.builder()
                .title(title)
                .choice1(choice1)
                .choice2(choice2)
                .choice3(choice3)
                .choice4(choice4)
                .answer(answer)
                .explanation(explanation)
                .difficultyType(difficultyType)
                .chapterType(chapterType)
                .build();

        questionRepository.save(question);

        memberQuestionService.createMemberQuestion(member, question);
        return new ResponseEntity<>(
                new QuestionSet(
                        question.getId(),
                        question.getTitle(),
                        question.getChoice1(),
                        question.getChoice2(),
                        question.getChoice3(),
                        question.getChoice4(),
                        question.getAnswer(),
                        question.getExplanation(),
                        question.getDifficulty().toString(),
                        question.getChapter().toString()
                ),
                HttpStatus.OK
        );
    }
}
