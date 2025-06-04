package com.ema.ema_backend.domain.question.repository;

import com.ema.ema_backend.domain.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(
            value = """
            SELECT * FROM question q
            WHERE q.id NOT IN (
                SELECT mq.question_id
                FROM member_question mq
                WHERE mq.member_id = :memberId
            )
            ORDER BY RAND()
            LIMIT 3
        """,
            nativeQuery = true
    )
    List<Question> find3RandomQuestionsNotSolvedByMember(@Param("memberId") Long memberId);

    @Query(
            value = """
            SELECT * FROM question q
            WHERE q.id NOT IN (
                SELECT mq.question_id
                FROM member_question mq
                WHERE mq.member_id = :memberId
            ) AND q.chapter = :chapter
            ORDER BY RAND()
            LIMIT 1
        """,
            nativeQuery = true
    )
    Optional<Question> findByChapterName(@Param("memberId") Long memberId, @Param("chapter") String chapter);
    Optional<Question> findById(Long id);
}
