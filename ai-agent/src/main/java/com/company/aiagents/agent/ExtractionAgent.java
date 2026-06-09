/// here added the Extractedwith AI and if fallback happen it is extracted with the regex patterns 
package com.company.aiagents.agent;

import com.company.aiagents.model.ExtractedData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExtractionAgent {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    // ── Regex fallback patterns (kept from original) ───────────────────────
    private static final Pattern FUND_NAME_PATTERN = Pattern.compile(
        "FUND(?:\\s+NAME)?[:\\s]+([\\w\\s&.,'-]+?)(?=[\\r\\n]|PAYMENT|TOTAL|AMOUNT|MANAGER|$)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static final Pattern AMOUNT_PATTERN = Pattern.compile(
        "(?:TOTAL(?:\\s+THIS\\s+CALL)?|AMOUNT\\s+DUE|CALL\\s+AMOUNT|NET\\s+DISTRIBUTION)" +
        "[:\\s]+\\$?([\\d,]+(?:\\.[\\d]{1,2})?)",
        Pattern.CASE_INSENSITIVE);

    public ExtractionAgent(ChatClient.Builder builder, ObjectMapper objectMapper) {
        this.chatClient = builder.build();
        this.objectMapper = objectMapper;
    }

    public ExtractedData extract(String text) {
        if (text == null || text.isBlank()) return new ExtractedData();

        try {
            // ── Primary: ask the AI ────────────────────────────────────────
            return extractWithAI(text);
        } catch (Exception e) {
            System.err.println("ExtractionAgent: AI failed, falling back to regex — " + e.getMessage());
            // ── Fallback: regex patterns ───────────────────────────────────
            return extractWithRegex(text);
        }
    }

    private ExtractedData extractWithAI(String text) throws Exception {
        String prompt = """
                You are a financial document parser.
                Extract the following fields from the document text below.
                Return ONLY valid JSON — no explanation, no markdown.
                {
                  "fundName": "...",
                  "manager": "...",
                  "amount": "...",
                  "dueDate": "...",
                  "documentType": "...",
                  "referenceNumber": "..."
                }
                Use null for missing fields. Amount as plain number, no $ or commas.
                
                Document:
                """ + text;

        String response = chatClient.prompt().user(prompt).call().content();
        String cleaned  = response
                .replaceAll("(?s)```json\\s*", "")
                .replaceAll("(?s)```\\s*", "")
                .trim();

        return objectMapper.readValue(cleaned, ExtractedData.class);
    }

    private ExtractedData extractWithRegex(String text) {
        String t = text.replace("\r\n", "\n").replace("\r", "\n");
        ExtractedData data = new ExtractedData();

        Matcher m = FUND_NAME_PATTERN.matcher(t);
        if (m.find()) data.setFundName(m.group(1).trim());

        m = AMOUNT_PATTERN.matcher(t);
        if (m.find()) data.setAmount(m.group(1).replace(",", ""));

        return data;
    }
}