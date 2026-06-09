package com.company.aiagents.service;

import com.company.aiagents.agent.EmailAgent;
import com.company.aiagents.agent.ExtractionAgent;
import com.company.aiagents.agent.PdfAgent;
import com.company.aiagents.agent.ExcelAgent;
import com.company.aiagents.model.ExtractedData;
import com.company.aiagents.repository.ProcessedDocumentRepository;
import com.company.aiagents.service.LocalStorageService;
import org.junit.jupiter.api.Test;

import jakarta.mail.Message;
import java.io.File;
import java.util.List;

import static org.mockito.Mockito.*;

class DocumentProcessingServiceTest {

    @Test
    void run_triggersPortalWorkflow_whenEmailBodyContainsPortalUrl() throws Exception {
        // Arrange
        EmailAgent emailAgent = mock(EmailAgent.class);
        PdfAgent pdfAgent = mock(PdfAgent.class);
        ExtractionAgent extractionAgent = mock(ExtractionAgent.class);
        ExcelAgent excelAgent = mock(ExcelAgent.class);
        S3StorageService s3 = mock(S3StorageService.class);
        LocalStorageService localStorage = mock(LocalStorageService.class);
        ProcessedDocumentRepository repo = mock(ProcessedDocumentRepository.class);
        PortalWorkflowService portalPipeline = mock(PortalWorkflowService.class);

        Message msg = mock(Message.class);
        when(emailAgent.fetchMessages()).thenReturn(List.of(msg));
        when(emailAgent.extractPdfs(msg)).thenReturn(List.of());
        when(emailAgent.extractBodyText(msg, false)).thenReturn("Please login at https://example-portal.com/login to download docs");

        DocumentProcessingService svc = new DocumentProcessingService(
            emailAgent, pdfAgent, extractionAgent, excelAgent, s3, localStorage, repo, portalPipeline
        );

        // Act
        svc.run();

        // Assert
        verify(portalPipeline, times(1)).runWithUrl("https://example-portal.com/login");
        verify(emailAgent, times(1)).markAsRead(msg);
    }
}
