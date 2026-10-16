package com.project.teman_belajar.module.quiz.dto.request;

import com.project.teman_belajar.module.summarize.dto.response.SummaryDetailResponse;

public record GenerateQuizRequest(
   SummaryDetailResponse summary
) {}
