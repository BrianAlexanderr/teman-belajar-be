package com.project.teman_belajar.module.llm.service;

import com.project.teman_belajar.module.llm.dto.response.GeneratedQuizResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

@AiService
public interface GeminiQuizService {

    @SystemMessage({
            "You are an expert educational content creator.",
            "Your task is to generate a well-structured multiple-choice quiz based on the provided summary.",
            "For the 'correctAnswer' field, you MUST provide the exact text string matching the correct choice. Do not use letters.",
            "Ensure explanations are concise."
    })
    @UserMessage("""
        Generate a quiz with {{questionCount}} questions based on the following context:
        
        Title: {{title}}
        Key Points: {{keyPoints}}
        Content: {{content}}
        """)
    GeneratedQuizResponse generateQuiz(
            @V("title") String title,
            @V("keyPoints") List<String> keyPoints,
            @V("content") String content,
            @V("questionCount") int questionCount
    );
}
