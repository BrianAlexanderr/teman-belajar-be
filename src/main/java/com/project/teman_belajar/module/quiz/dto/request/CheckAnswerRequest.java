package com.project.teman_belajar.module.quiz.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CheckAnswerRequest(
   String answer,
   UUID questionId
) {}
