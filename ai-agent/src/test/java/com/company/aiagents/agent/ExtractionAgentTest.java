package com.company.aiagents.agent;

import com.company.aiagents.model.ExtractedData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExtractionAgentTest {

    @Test
    void extract_fallsBackToRegexWhenAiThrowsException() {
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        when(builder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenThrow(new RuntimeException("AI failure"));

        ExtractionAgent agent = new ExtractionAgent(builder, new ObjectMapper());

        String text = "FUND NAME: Alpha Fund\nTOTAL THIS CALL: $12,345\n";
        ExtractedData data = agent.extract(text);

        assertEquals("Alpha Fund", data.getFundName());
        assertEquals("12345", data.getAmount());
    }
}
