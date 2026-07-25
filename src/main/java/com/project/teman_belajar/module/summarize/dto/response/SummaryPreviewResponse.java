package com.project.teman_belajar.module.summarize.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SummaryPreviewResponse(
    UUID id,

    String title,

    String preview
){}
