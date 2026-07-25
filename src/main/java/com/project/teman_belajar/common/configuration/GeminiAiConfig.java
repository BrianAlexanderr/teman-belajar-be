package com.project.teman_belajar.common.configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class GeminiAiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model.name}")
    private String modelName;

    @Value("${gemini.temperature}")
    private Double temperature;

    @Value("${gemini.timeout.seconds}")
    private long timeoutSeconds;

    @Bean
    public ChatModel geminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .maxRetries(3)
                .build();
    }
}
