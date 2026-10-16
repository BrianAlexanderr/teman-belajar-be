package com.project.teman_belajar.module.llm.dto.response;

import java.util.List;

public record GeneratedQuizResponse(
        String title,
        List<GeneratedQuestion> questions
) {
    public record GeneratedQuestion(
            String question,
            String answerA,
            String answerB,
            String answerC,
            String answerD,
            String correctAnswer,
            String explanation
    ) {}
}
