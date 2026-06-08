package com.company.aiagents.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class AiService {

    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;

    public AiService(WebClient.Builder webClientBuilder,
                     @Value("${ai.openai.api-key:}") String apiKey,
                     @Value("${ai.openai.base-url:https://api.openai.com}") String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.webClient = webClientBuilder.baseUrl(this.baseUrl).build();
    }

    public String chatCompletion(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set. Set environment variable OPENAI_API_KEY.");
        }

        ChatRequest.Message userMessage = new ChatRequest.Message("user", prompt);
        ChatRequest request = new ChatRequest("gpt-3.5-turbo", List.of(userMessage));

        Mono<String> resp = webClient.post()
            .uri("/v1/chat/completions")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(ChatResponse.class)
            .map(chatResponse -> {
                if (chatResponse.choices != null && !chatResponse.choices.isEmpty()) {
                    return chatResponse.choices.get(0).message.content.trim();
                }
                return "";
            });

        return resp.block();
    }

    // --- DTOs (minimal) ---
    public static class ChatRequest {
        public String model;
        public List<Message> messages;

        public ChatRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }

        public static class Message {
            public String role;
            public String content;

            public Message() {}

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
    }

    public static class ChatResponse {
        public List<Choice> choices;

        public static class Choice {
            public ChatRequest.Message message;
        }
    }
}
