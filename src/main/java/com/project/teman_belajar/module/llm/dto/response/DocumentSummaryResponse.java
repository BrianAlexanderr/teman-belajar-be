package com.project.teman_belajar.module.llm.dto.response;


import dev.langchain4j.model.output.structured.Description;

import java.util.List;

public record DocumentSummaryResponse(
        @Description("A short, catchy title representing the document content")
        String title,

        @Description("A 1-2 sentence brief preview or teaser of the summary")
        String preview,

        @Description("A list of the top 3 to 5 most important bullet points or key takeaways")
        List<String> keyPoints,

        @Description("The complete, detailed comprehensive summarization formatted in clean paragraphs")
        String content
) {}
