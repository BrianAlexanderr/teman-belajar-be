package com.project.teman_belajar.module.quiz.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PreviewQuestion(
        UUID id,
        String question,
        String answerA,
        String answerB,
        String answerC,
        String answerD
) {}
