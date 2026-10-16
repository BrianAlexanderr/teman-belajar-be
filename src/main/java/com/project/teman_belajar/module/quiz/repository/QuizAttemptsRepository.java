package com.project.teman_belajar.module.quiz.repository;

import com.project.teman_belajar.module.quiz.entities.QuizAttempts;
import com.project.teman_belajar.module.quiz.projection.QuizStats;
import io.lettuce.core.dynamic.annotation.Param;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizAttemptsRepository extends JpaRepository<QuizAttempts, UUID> {

    @Query("SELECT qa FROM QuizAttempts qa " +
            "JOIN FETCH qa.quiz q " +
            "LEFT JOIN FETCH q.summary s " +
            "LEFT JOIN FETCH s.folders f " +
            "WHERE qa.user.id = :userId")
    List<QuizAttempts> findByUserIdWithRelations(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = {"quiz", "questionAttempts", "questionAttempts.question"})
    @Override
    @NonNull Optional<QuizAttempts> findById(@NonNull UUID id);

    @Query("""
        SELECT qa FROM QuizAttempts qa
        WHERE qa.quiz.id = :quizId
          AND qa.user.id = :userId
          AND qa.score IS NULL
        ORDER BY qa.createdAt DESC
    """)
    Optional<QuizAttempts> findFirstDraft(
            @Param("quizId") UUID quizId,
            @Param("userId") UUID userId
    );

    @Query("SELECT COUNT(qa.id) AS totalAttempts, AVG(qa.score) AS averageScore " +
            "FROM QuizAttempts qa " +
            "WHERE qa.user.id = :userId AND qa.score IS NOT NULL")
    QuizStats getUserQuizStats(@Param("userId") UUID userId);
}
