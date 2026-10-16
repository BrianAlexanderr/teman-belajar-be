package com.project.teman_belajar.module.quiz.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AttemptedQuizResponse(
        UUID quizId,
        UUID attemptedQuizId,
        String quizTitle,
        String folderName,
        String score,
        String countAttemptedQuestion,
        String attemptedAt
) {}
