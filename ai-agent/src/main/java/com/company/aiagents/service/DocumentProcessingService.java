// package com.company.aiagents.service;

// import com.company.aiagents.agent.*;
// import com.company.aiagents.model.*;

// import java.io.File;

// public class DocumentProcessingService {

//     private final PdfAgent pdfAgent =
//             new PdfAgent();

//     private final ExtractionAgent extractionAgent =
//             new ExtractionAgent();

//     private final ExcelAgent excelAgent =
//             new ExcelAgent();

//     public void process(File pdf)
//             throws Exception {

//         String text =
//                 pdfAgent.extractText(pdf);

//         ExtractedData data =
//                 extractionAgent.extract(text);

//         excelAgent.append(data);
//     }
// }

package com.company.aiagents.service;

import com.company.aiagents.agent.*;
import com.company.aiagents.model.ExtractedData;
import com.company.aiagents.repository.ProcessedDocumentRepository;
import jakarta.mail.Message;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentProcessingService {

    private static final Pattern URL_PATTERN = Pattern.compile("(https?://[^\\s\"'<>]+)", Pattern.CASE_INSENSITIVE);

    private final EmailAgent emailAgent;
    private final PdfAgent pdfAgent;
    private final ExtractionAgent extractionAgent;
    private final ExcelAgent excelAgent;
    private final S3StorageService s3;
    private final LocalStorageService localStorage;
    private final ProcessedDocumentRepository repo;
    private final PortalWorkflowService portalPipeline;

    public DocumentProcessingService(
            EmailAgent emailAgent, PdfAgent pdfAgent,
            ExtractionAgent extractionAgent, ExcelAgent excelAgent,
            S3StorageService s3, LocalStorageService localStorage,
            ProcessedDocumentRepository repo,
            PortalWorkflowService portalPipeline) {
        this.emailAgent = emailAgent;
        this.pdfAgent = pdfAgent;
        this.extractionAgent = extractionAgent;
        this.excelAgent = excelAgent;
        this.s3 = s3;
        this.localStorage = localStorage;
        this.repo = repo;
        this.portalPipeline = portalPipeline;
    }

    public void run() throws Exception {
        List<Message> messages = emailAgent.fetchMessages();

        for (Message message : messages) {
            List<File> pdfs = emailAgent.extractPdfs(message);
            String bodyText = emailAgent.extractBodyText(message, false);
            String portalUrl = findPortalUrl(bodyText);

            if (portalUrl != null) {
                System.out.println("DocumentProcessingService: portal URL found in email body, starting portal workflow for " + portalUrl);
                portalPipeline.runWithUrl(portalUrl);
            }

            if (!pdfs.isEmpty()) {
                // Path A: email has PDF attachments
                for (File pdf : pdfs) {
                    if (repo.alreadyProcessed(pdf.getName())) {
                        System.out.println("Skipping already-processed: " + pdf.getName());
                        continue;
                    }
                    String text = pdfAgent.extractText(pdf);
                    ExtractedData data = extractionAgent.extract(text);
                    localStorage.save(pdf, data);
                    String s3Folder = "raw-pdfs/" + localStorage.buildRelativePath(data);
                    s3.upload(pdf, s3Folder);
                    excelAgent.append(pdf, data);
                    repo.markProcessed(pdf.getName());
                }
            } else {
                // Path B: no PDF — extract from email body text
                if (!bodyText.isBlank()) {
                    ExtractedData data = extractionAgent.extract(bodyText);
                    excelAgent.append(data);
                    emailAgent.markAsRead(message);
                }
            }
        }
    }

    private String findPortalUrl(String bodyText) {
        if (bodyText == null || bodyText.isBlank()) {
            return null;
        }

        Matcher matcher = URL_PATTERN.matcher(bodyText);
        while (matcher.find()) {
            String url = matcher.group(1);
            if (url.toLowerCase().contains("portal")) {
                return url;
            }
        }

        return null;
    }
}