package com.company.aiagents.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${spring.ai.chat.options.temperature:0.0}")
    private double temperature;

    @Value("${spring.ai.chat.options.max-tokens:1000}")
    private int maxTokens;

    // ── Build a ChatClient bean with default options ───────────────────────
    // Spring AI auto-configures the underlying ChatModel (OpenAI or Anthropic)
    // based on whichever starter you added in pom.xml.
    // ExtractionAgent receives this ChatClient via constructor injection.
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultOptions(
                    ChatOptions.builder()
                        .temperature(temperature)    // 0.0 = deterministic, best for data extraction
                        .maxTokens(maxTokens)        // limit response length
                        .build()
                )
                .defaultSystem("""
                        You are a financial document data extraction specialist.
                        You extract structured data from fund documents such as
                        capital call notices, distribution notices, and quarterly statements.
                        Always respond with valid JSON only.
                        Never add explanations, markdown, or code fences.
                        If a field is not found in the document, set its value to null.
                        """)
                .build();
    }
}