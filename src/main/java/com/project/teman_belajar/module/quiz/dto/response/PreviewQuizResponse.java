package com.project.teman_belajar.module.quiz.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PreviewQuizResponse(
   UUID id,
   String title,
   String createdAt
) {}
