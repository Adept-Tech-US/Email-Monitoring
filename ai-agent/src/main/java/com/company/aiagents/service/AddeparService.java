package com.company.aiagents.service;

import java.io.File;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AddeparService {

    private final WebClient webClient;
    private final String apiKey;
    private final String importPath;
    private final boolean enabled;
    private final Duration timeout;

    public AddeparService(
            WebClient.Builder webClientBuilder,
            @Value("${addepar.enabled:false}") boolean enabled,
            @Value("${addepar.api.base-url:}") String apiBaseUrl,
            @Value("${addepar.api.token:}") String apiToken,
            @Value("${addepar.api.import-path:/api/v1/import}") String importPath,
            @Value("${addepar.api.timeout-ms:30000}") long timeoutMs) {
        this.enabled = enabled && apiBaseUrl != null && !apiBaseUrl.isBlank() && apiToken != null && !apiToken.isBlank();
        this.apiKey = apiToken;
        this.importPath = importPath;
        this.timeout = Duration.ofMillis(timeoutMs);
        this.webClient = webClientBuilder.baseUrl(apiBaseUrl).build();
    }

    public void importExcel(File reportFile) {
        if (!enabled) {
            System.out.println("AddeparService: disabled or missing configuration, skipping report upload.");
            return;
        }

        if (reportFile == null || !reportFile.exists()) {
            System.err.println("AddeparService: report file does not exist, skipping upload.");
            return;
        }

        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("file", reportFile, MediaType.APPLICATION_OCTET_STREAM)
                    .filename(reportFile.getName());
            bodyBuilder.part("filename", reportFile.getName());

            String response = webClient.post()
                    .uri(importPath)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(timeout);

            System.out.println("AddeparService: report uploaded successfully, response=" + response);
        } catch (Exception e) {
            System.err.println("AddeparService: failed to upload report to Addepar — " + e.getMessage());
        }
    }
}
