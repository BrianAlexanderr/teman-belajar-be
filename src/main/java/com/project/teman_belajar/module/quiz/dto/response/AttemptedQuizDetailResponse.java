package com.project.teman_belajar.module.quiz.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record AttemptedQuizDetailResponse(
    UUID quizId,
    List<QuestionAttemptResponse> questionAttemptResponse
) {

    @Builder
    public record QuestionAttemptResponse(
            UUID questionId,
            Boolean isCorrect,
            String selectedAnswer,
            String explanation
    ){}
}
