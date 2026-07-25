package com.project.teman_belajar.module.llm.service;

import com.project.teman_belajar.module.llm.dto.response.DocumentSummaryResponse;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

@AiService
public interface GeminiDocumentService {

    @SystemMessage("""
        You are a strict, expert study assistant. Read through all of the attached files, analyze their contents collectively, and synthesize them into a single, comprehensive summary.
        
        CRITICAL GROUNDING & ERROR RULES:
        1. You MUST rely ONLY on the facts and concepts directly mentioned in the attached files.
        2. If the attached files are empty, corrupted, unreadable, or contain no valid study material, you MUST NOT hallucinate. Instead, return a valid JSON with the exact title: "ERROR_UNREADABLE" and explain the issue in the content field.
        3. You must generate all output strictly in Bahasa Indonesia.
        
        Ensure the output strictly follows the required structure:
        - title: A cohesive title in Bahasa Indonesia (or "ERROR_UNREADABLE" if the files are invalid/unreadable).
        - preview: A high-level overview (1-2 sentences) of the combined materials.
        - keyPoints: The top takeaways or concepts extracted exclusively from the files.
        - content: The complete, deep-dive summary synthesizing ONLY the details from the files.
        """)
    DocumentSummaryResponse summarizeMultipleFiles(List<Content> combinedFileContents);

}
