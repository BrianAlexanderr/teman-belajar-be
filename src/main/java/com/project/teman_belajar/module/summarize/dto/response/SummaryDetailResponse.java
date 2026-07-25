package com.project.teman_belajar.module.summarize.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SummaryDetailResponse(
      UUID id,
      String title,
      List<String> keyPoint,
      String content
) {}
