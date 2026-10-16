package com.project.teman_belajar.module.quiz.dto.response;

import lombok.Builder;

@Builder
public record CheckAnswerResponse(
    String correctAnswer,
    Boolean isCorrect,
    String correctAnswerField,
    String explanation
) {}
