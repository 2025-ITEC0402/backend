package com.ema.ema_backend.domain.member.service;

import com.ema.ema_backend.domain.member.dto.LearningRecommendation;
import com.ema.ema_backend.domain.member.dto.MemberInfoResponse;
import com.ema.ema_backend.domain.member.dto.StreakInfoResponse;
import com.ema.ema_backend.domain.member.dto.StreakSet;
import com.ema.ema_backend.domain.member.entity.LearningHistory;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.member.repository.LearningHistoryRepository;
import com.ema.ema_backend.domain.member.repository.MemberRepository;
import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import com.ema.ema_backend.domain.memberquestion.service.MemberQuestionService;
import com.ema.ema_backend.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final LearningHistoryRepository learningHistoryRepository;
    private final MemberQuestionService memberQuestionService;
    private final GeminiService geminiService;
    private final JsonParsingService jsonParsingService;

    private String updateLearningHistoryPrompt = "당신은 최고의 공학수학 학습 컨설턴트입니다.\n당신은 사용자의 기존 학습 이력을 보고, 사용자가 더 나은 공학수학 능력을 가질 수 있도록 추천할 단원을 정하여 알려주면 됩니다.\n\n단원에 대한 정보는 다음과 같습니다.\n1 : \"함수와 모델 (Functions and Models)\",\n2 : \"극한과 도함수 (Limits and Derivatives)\",\n3 : \"미분 법칙 (Differentiation Rules)\",\n4 : \"미분의 응용 (Applications of Differentiation)\",\n5 : \"적분 (Integrals)\",\n6 : \"적분의 응용 (Applications of Integration)\",\n7 : \"적분 기법 (Techniques of Integration)\",\n8 : \"적분의 추가 응용 (Further Applications of Integration)\",\n9 : \"미분방정식 (Differential Equations)\",\n10 : \"매개변수 방정식과 극좌표 (Parametric Equations and Polar Coordinates)\",\n11 : \"무한 수열과 급수 (Infinite Sequences and Series)\",\n12 : \"벡터와 공간 기하학 (Vectors and the Geometry of Space)\",\n13 : \"벡터 함수 (Vector Functions)\",\n14 : \"편미분 (Partial Derivatives)\",\n15 : \"다중 적분 (Multiple Integrals)\",\n16 : \"벡터 미적분학 (Vector Calculus)\",\n17 : \"2계 미분방정식 (Second-Order Differential Equations)\"\n\n사용자의 정보로는 \"각 단원 별 정답률\", \"이전의 추천된 학습 단원 3개\", \"개념 학습한 단원 목록\" 이 주어집니다.\n\n\"각 단원 별 정답률\" 은 \"단원 : 정답 문제 수 / 푼 문제 수\" 형태의 문자열로 주어집니다.\n\"이전의 추천된 학습 단원 3개\" 는 1 ~ 17 사이의 정수형 숫자 3개가 주어집니다.\n\"개념 학습한 단원 목록\" 은 1 ~ 17 사이의 정수형 숫자가 최소 0개에서 최대 17개가 주어집니다.\n\n<입력 예시>\n{\n  \"각 단원 별 정답률\" : {\n    1 : 8 / 10,\n    2 : 3 / 7,\n    3 : 6 / 6,\n    4 : 0 / 0,\n    5 : 0 / 0,\n    6 : 0 / 0,\n    7 : 0 / 0,\n    8 : 0 / 0,\n    9 : 0 / 0,\n    10 : 0 / 0,\n    11 : 0 / 0,\n    12 : 0 / 0,\n    13 : 0 / 0,\n    14 : 0 / 0,\n    15 : 0 / 0,\n    16 : 0 / 0,\n    17 : 0 / 0\n  },\n  \"이전의 추천된 학습 단원 3개\" : \"1 2 3\",\n  \"개념 학습한 단원 목록\" : \"1 2 3 4 5\"\n}\n\n당신은 위와 같은 입력을 보고 다음과 같은 출력을 내보내야 합니다.\n\n<출력 필드 설명>\nrecommendedChapter1 : 추천하는 단원 3개 중 첫 번째 단원 숫자를 담아주세요.\nrecommendedChapter2 : 추천하는 단원 3개 중 두 번째 단원 숫자를 담아주세요.\nrecommendedChapter3 : 추천하는 단원 3개 중 세 번째 단원 숫자를 담아주세요.\nlearningLevel : 사용자의 공학수학 학습능력을 판단하여, \"BEGINNER\", \"INTERMEDIATE\", \"ADVANCED\", \"EXPERT\" 중 하나로 골라주세요.\ngoal : 사용자가 공학 수학을 더 잘할 수 있도록 학습 목표를 설정해주세요. 한글 한 문장으로 작성해주세요.\n\n**출력 형식은 '{'로 시작하여 '}'로 끝나는 결과만 허용합니다. 마크다운 형태로 출력하지 말고 순수 String 형태로 출력하세요. 다른 태그는 일절 달지마세요.**\n<출력 예시>\n{\n    \"recommendedChapter1\" : \"3\",\n    \"recommendedChapter2\" : \"5\",\n    \"recommendedChapter3\" : \"17\",\n    \"learningLevel\" : \"BEGINNER\",\n    \"goal\" : \"전체적인 문제의 정답률을 높여야 할 것 같고, 특히 2단원을 더 공부해보면 좋을 것 같아요.\"\n}\n이제 아래 실제 사용자 정보에 대해 분석하고, 다음 규칙을 반드시 지켜서 응답해 줘.\n" +
            "1. 응답은 반드시 유효한 JSON 형식이어야 한다.\n" +
            "2. 응답은 반드시 '{' 문자로 시작하고 '}' 문자로 끝나야 한다.\n" +
            "3. Markdown 코드 블록(```)이나 다른 설명은 절대 포함하지 마라.\n" +
            "4. 오직 JSON 데이터만 출력하라.";

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

    @Transactional
    public ResponseEntity<Void> updateLearningHistory(Authentication authentication){
        Optional<Member> optionalMember = checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", " ");
        }
        Member member = optionalMember.get();

        // 사용자의 정보 조립
        String userData = getCorrectRatePerChapter(member);
        System.out.println(userData);

        // RestTemplate 으로 LLM 호출 후 학습 이력 갱신하기
        System.out.println(updateLearningHistoryPrompt);


        String geminiOutput = geminiService.getGeminiResponse(updateLearningHistoryPrompt + userData);
        System.out.println("Gemini 결과 " + geminiOutput);
        // 결과 json 파싱
        LearningRecommendation dto = jsonParsingService.parse(geminiOutput);

        System.out.println(dto);

        member.getLearningHistory().update(dto.recommendedChapter1(), dto.recommendedChapter2(), dto.recommendedChapter3(), dto.goal(), member);
//        String chapter1 = "3";
//        String chapter2 = "6";
//        String chapter3 = "7";
//        String goal = "변경된 목표입니다.";
//
//        member.getLearningHistory().update(chapter1, chapter2, chapter3, goal, member);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getCorrectRatePerChapter(Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        Map<Integer, List<MemberQuestion>> questionsByChapter = member.getMemberQuestions().stream()
                .collect(Collectors.groupingBy(mq -> Integer.parseInt(mq.getQuestion().getChapter().toString().substring(8))));

        sb.append("  \"각 단원 별 정답률\" : {\n");
        for (int i = 1; i <= 17; i++) {
            // 해당 단원에 풀이 기록이 있는지 확인
            if (questionsByChapter.containsKey(i)) {
                List<MemberQuestion> chapterQuestions = questionsByChapter.get(i);
                long totalAttempts = chapterQuestions.size();
                long correctAnswers = chapterQuestions.stream().filter(MemberQuestion::getCorrectOnFirstTry).count();
                sb.append(String.format("    %d : %d / %d", i, correctAnswers, totalAttempts));
            } else {
                // 풀이 기록이 없으면 0 / 0 으로 표시
                sb.append(String.format("    %d : 0 / 0", i));
            }

            // 마지막 단원이 아니면 쉼표 추가
            if (i < 17) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        sb.append("  },\n");
        LearningHistory history = member.getLearningHistory();
        String recommendedChapters = String.format("%s %s %s",
                history.getRecommendedChapter1().toString().substring(8),
                history.getRecommendedChapter2().toString().substring(8),
                history.getRecommendedChapter3().toString().substring(8));

        String completedConcepts = history.getCompletedChapters().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

        // 4. 추가 정보 문자열에 추가
        sb.append(String.format("  \"이전의 추천된 학습 단원 3개\" : \"%s\",\n", recommendedChapters));
        sb.append(String.format("  \"개념 학습한 단원 목록\" : \"%s\"\n", completedConcepts));

        sb.append("}");
        return sb.toString();
    }

    @Transactional
    public ResponseEntity<Void> postCompletedChapter(Integer chapterId, Authentication authentication){
        Optional<Member> optionalMember = checkPermission(authentication);
        if (optionalMember.isEmpty()) {
            throw new NotFoundException("Member", " ");
        }
        Member member = optionalMember.get();

        // 2. 학습 이력(LearningHistory) 존재 여부 확인 및 NPE 방지
        LearningHistory learningHistory = member.getLearningHistory();
        if (learningHistory == null) {
            throw new NotFoundException("LearningHistory", " ");
        }

        boolean isAdded = learningHistory.getCompletedChapters().add(chapterId);

        if (!isAdded) {
            System.out.println("Chapter " + chapterId + " was already completed.");
            return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
