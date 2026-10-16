package com.project.teman_belajar.module.quiz.dto.request;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record QuizAttemptRequest(
        UUID quizId,
        List<QuestionAttemptRequest> questionAttempts
) {
    @Builder
    public record QuestionAttemptRequest(
            UUID questionId,
            Boolean isCorrect,
            String selectedAnswer
    ){}
}
