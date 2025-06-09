package com.ema.ema_backend.domain.question.service;

import com.ema.ema_backend.domain.chatroom.dto.PyPostChatFirstResponse;
import com.ema.ema_backend.domain.chatroom.dto.PyPostChatRequest;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.service.AuthenticationService;
import com.ema.ema_backend.domain.member.service.MemberService;
import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import com.ema.ema_backend.domain.memberquestion.service.MemberQuestionService;
import com.ema.ema_backend.domain.question.Question;
import com.ema.ema_backend.domain.question.dto.*;
import com.ema.ema_backend.domain.question.repository.QuestionRepository;
import com.ema.ema_backend.domain.type.ChapterType;
import com.ema.ema_backend.domain.type.DifficultyType;
import com.ema.ema_backend.global.exception.NotFoundException;
import com.ema.ema_backend.global.exception.RemoteApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberQuestionService memberQuestionService;
    private final RestTemplate restTemplate;
    private final AuthenticationService authenticationService;

    @Value("${PY_SERVER_BASE_URI}")
    private String baseUri;

    public ResponseEntity<RecommendedQuestionInfoResponse> getRecommendQuestion(Authentication authentication) {
        Optional<Member> optionalMember = authenticationService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", "at QuestionService - getRecommendQuestion()");
        }
        Member member = optionalMember.get();

        List<RecommendSet> recommendSets = new ArrayList<>();

        ChapterType chapterType1 = member.getLearningHistory().getRecommendedChapter1();
        RecommendSet qs1 = new RecommendSet(member.getLearningHistory().getRecommendedQuestion1(), chapterType1.getChapterName(), chapterType1.toString(), "EASY");
        recommendSets.add(qs1);


        ChapterType chapterType2 = member.getLearningHistory().getRecommendedChapter2();
        RecommendSet qs2 = new RecommendSet(member.getLearningHistory().getRecommendedQuestion2(), chapterType2.getChapterName(), chapterType2.toString(), "NORMAL");
        recommendSets.add(qs2);

        ChapterType chapterType3 = member.getLearningHistory().getRecommendedChapter3();
        RecommendSet qs3 = new RecommendSet(member.getLearningHistory().getRecommendedQuestion3(), chapterType3.getChapterName(), chapterType3.toString(), "HARD");
        recommendSets.add(qs3);

        RecommendedQuestionInfoResponse response = new RecommendedQuestionInfoResponse(recommendSets);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<QuestionsInfoResponse> get3RandomQuestions(Authentication authentication) {
        Optional<Member> optionalMember = authenticationService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", "at QuestionService - get3RandomQuestions()");
        }
        Member member = optionalMember.get();

        List<Question> questionList = questionRepository.find3RandomQuestionsNotSolvedByMember(member.getId());

        List<QuestionSet> questions = new ArrayList<>();

        for (Question q : questionList) {
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
                            q.getAiSummary(),
                            q.getDifficulty().toString(),
                            q.getChapter().toString()));
        }
        return ResponseEntity.ok(new QuestionsInfoResponse(questions));
    }

    @Transactional
    public ResponseEntity<Void> deleteMyQuestions(Authentication authentication) {
        Optional<Member> optionalMember = authenticationService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", "at QuestionService - deleteMyQuestions()");
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
    public PersonalizedQuestionGeneratedResponse generatePersonalizedQuestion(String userData, Member member) {
        // 학습 이력 정제하기
        // 사용자가 풀이한 이력 + 사용자의 선호 난이도 정도 제공?


        // 1번 문제 생성 - RestTemplate 로 파이썬 서버 API 이용하고 결과 받아오기
        PersonalizedQuestionRequest request = new PersonalizedQuestionRequest(
                member.getLearningHistory().getRecommendedChapter1().toString(),
                member.getLearningHistory().getRecommendedChapter1().getChapterName(),
                userData,
                member.getLearningHistory().getLearningLevel().toString(),
                "");
        PersonalizedQuestionResponse response = postGeneratePersonalizedQuestionToPy(request);
        System.out.println("첫 번째 문제 생성 완료... ");
        // 결과를 Question 에 저장
        Question q1 = Question.builder()
                .title(response.question())
                .choice1(response.choice1())
                .choice2(response.choice2())
                .choice3(response.choice3())
                .choice4(response.choice4())
                .answer(response.answer())
                .explanation(response.solution())
                .difficultyType(DifficultyType.valueOf(response.difficulty()))
                .chapterType(ChapterType.getChapterType(response.chapter()))
                .aiSummary(response.ai_summary())
                .build();
        questionRepository.save(q1);

        // 2번 문제 생성
        request = new PersonalizedQuestionRequest(
                member.getLearningHistory().getRecommendedChapter2().toString(),
                member.getLearningHistory().getRecommendedChapter2().getChapterName(),
                userData,
                member.getLearningHistory().getLearningLevel().toString(),
                "");
        response = postGeneratePersonalizedQuestionToPy(request);

        // 결과를 Question 에 저장
        Question q2 = Question.builder()
                .title(response.question())
                .choice1(response.choice1())
                .choice2(response.choice2())
                .choice3(response.choice3())
                .choice4(response.choice4())
                .answer(response.answer())
                .explanation(response.solution())
                .difficultyType(DifficultyType.valueOf(response.difficulty()))
                .chapterType(ChapterType.getChapterType(response.chapter()))
                .aiSummary(response.ai_summary())
                .build();
        questionRepository.save(q2);

        // 2번 문제 생성
        request = new PersonalizedQuestionRequest(
                member.getLearningHistory().getRecommendedChapter3().toString(),
                member.getLearningHistory().getRecommendedChapter3().getChapterName(),
                userData,
                member.getLearningHistory().getLearningLevel().toString(),
                "");
        response = postGeneratePersonalizedQuestionToPy(request);

        // 결과를 Question 에 저장
        Question q3 = Question.builder()
                .title(response.question())
                .choice1(response.choice1())
                .choice2(response.choice2())
                .choice3(response.choice3())
                .choice4(response.choice4())
                .answer(response.answer())
                .explanation(response.solution())
                .difficultyType(DifficultyType.valueOf(response.difficulty()))
                .chapterType(ChapterType.getChapterType(response.chapter()))
                .aiSummary(response.ai_summary())
                .build();
        questionRepository.save(q3);
        return new PersonalizedQuestionGeneratedResponse(q1.getId(), q2.getId(), q3.getId());
    }

    @Transactional
    public ResponseEntity<QuestionSet> getQuestionById(Long id, Authentication authentication) {
        Optional<Member> optionalMember = authenticationService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", "at QuestionService - getQuestionById()");
        }
        Member member = optionalMember.get();

        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new NotFoundException("Question", "at QuestionService - getQuestionById()");
        }
        Question question = optionalQuestion.get();

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
                        question.getAiSummary(),
                        question.getDifficulty().toString(),
                        question.getChapter().toString()
                ),
                HttpStatus.OK
        );
    }

    @Transactional
    public ResponseEntity<Void> checkAnswer(Long id, CheckAnswerRequest req, Authentication authentication) {
        Optional<Member> optionalMember = authenticationService.checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", "at QuestionService - checkAnswer()");
        }
        Member member = optionalMember.get();

        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new NotFoundException("Question", "at QuestionService - checkAnswer()");
        }
        Question question = optionalQuestion.get();

        memberQuestionService.createMemberQuestion(member, question, Boolean.valueOf(req.correctOnFirstTry()));
        return ResponseEntity.ok().build();
    }


    @Transactional
    public PersonalizedQuestionResponse postGeneratePersonalizedQuestionToPy(PersonalizedQuestionRequest req){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity에 바디와 헤더 담기
        HttpEntity<PersonalizedQuestionRequest> httpEntity = new HttpEntity<>(req, headers);

        // RestTemplate으로 POST 요청 보내기
        ResponseEntity<PersonalizedQuestionResponse> responseEntity = restTemplate.postForEntity(
                baseUri + "/newquestions",
                httpEntity,
                PersonalizedQuestionResponse.class
        );

        // HTTP 상태 코드 체크 (200 OK 등)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // 8) 바디에 담긴 QuestionResponse 객체 꺼내기
            return responseEntity.getBody();
        } else {
            // 오류 응답 처리 (예외 던지기 등)
            throw new RemoteApiException("원격 서버 응답 실패: HTTP " + responseEntity.getStatusCode() + "," + baseUri + "/newquestions");
        }
    }
}
