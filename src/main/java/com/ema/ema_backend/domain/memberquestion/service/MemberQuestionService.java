package com.ema.ema_backend.domain.memberquestion.service;

import com.ema.ema_backend.domain.member.dto.MemberInfoResponse;
import com.ema.ema_backend.domain.member.dto.StreakSet;
import com.ema.ema_backend.domain.member.entity.Member;
import com.ema.ema_backend.domain.memberquestion.MemberQuestion;
import com.ema.ema_backend.domain.memberquestion.repository.MemberQuestionRepository;
import com.ema.ema_backend.domain.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberQuestionService {
    private final MemberQuestionRepository memberQuestionRepository;

    public void createMemberQuestion(Member member, Question question, Boolean correctOnFirstTry) {
        MemberQuestion memberQuestion = memberQuestionRepository.save(new MemberQuestion(member, question, correctOnFirstTry));
        member.getMemberQuestions().add(memberQuestion);
        question.getQuestionList().add(memberQuestion);
    }

    public void deleteMemberQuestion(MemberQuestion mq) {
        memberQuestionRepository.delete(mq);
    }

    @Transactional
    public List<StreakSet> getStreakInfo(Member member){
        List<MemberQuestion> memberQuestionList = member.getMemberQuestions();

        // solvedAt 필드(예: LocalDateTime)에서 날짜 부분(LocalDate)만 추출하여 카운트
        Map<LocalDate, Long> solvedCountByDate = memberQuestionList.stream()
                .map(mq -> mq.getSolvedAt().toLocalDate())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Map<LocalDate, Long>을 List<StreakSet>으로 변환하면서 날짜 순으로 정렬
        return solvedCountByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new StreakSet(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberInfoResponse getMemberInfo(Member member) {
        List<MemberQuestion> memberQuestionList = member.getMemberQuestions();

        // 1) 전체 푼 문제 개수
        int allTimeSolved = memberQuestionList.size();

        // 2) 오늘 푼 문제 개수
        LocalDate today = LocalDate.now();
        int todaySolved = (int) memberQuestionList.stream()
                .map(mq -> mq.getSolvedAt().toLocalDate())   // LocalDateTime -> LocalDate
                .filter(date -> date.equals(today))
                .count();

        // 3) 연속으로 푼 일수 계산
        Set<LocalDate> solvedDates = memberQuestionList.stream()
                .map(mq -> mq.getSolvedAt().toLocalDate())
                .collect(Collectors.toSet());

        //    - 오늘부터 뒤로 한 날씩 내려가면서, 푼 날짜가 연속으로 존재하는 한 카운트
        int streakDays = 0;
        LocalDate cursor = today;
        while (solvedDates.contains(cursor)) {
            streakDays++;
            cursor = cursor.minusDays(1);
        }
        return new MemberInfoResponse(member.getName(), todaySolved, allTimeSolved, streakDays);
    }
}
