package com.company.aiagents.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddeparServiceTest {

    @Test
    void importExcel_postsMultipartFormDataWhenEnabled(@TempDir Path tempDir) throws Exception {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(builder.baseUrl("https://api.addepar.test")).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("/api/v1/import")).thenReturn(bodySpec);
        when(bodySpec.header(HttpHeaders.AUTHORIZATION, "Bearer test-token")).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(bodySpec);
        when(bodySpec.body(any(BodyInserter.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("ok"));

        AddeparService service = new AddeparService(
                builder,
                true,
                "https://api.addepar.test",
                "test-token",
                "/api/v1/import",
                1000L);

        File reportFile = tempDir.resolve("report.xlsx").toFile();
        assertTrue(reportFile.createNewFile());

        service.importExcel(reportFile);

        verify(webClient).post();
        verify(bodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer test-token");
        verify(bodySpec).contentType(MediaType.MULTIPART_FORM_DATA);
        verify(bodySpec).body(any(BodyInserter.class));
        verify(headersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }
}
