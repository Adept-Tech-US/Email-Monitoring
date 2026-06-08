// package com.company.aiagents.service;

// import com.company.aiagents.agent.*;
// import com.company.aiagents.model.ExtractedData;
// import jakarta.mail.*;
// import jakarta.mail.internet.MimeUtility;
// import org.springframework.stereotype.Service;

// import java.io.File;
// import java.util.List;
// import java.util.Properties;

// @Service
// public class WorkflowService {

//     private final EmailAgent    emailAgent;
//     private final PdfAgent      pdfAgent;
//     private final ExtractionAgent extractionAgent;
//     private final ExcelAgent    excelAgent;

//     public WorkflowService(EmailAgent emailAgent,
//                            PdfAgent pdfAgent,
//                            ExtractionAgent extractionAgent,
//                            ExcelAgent excelAgent) {
//         this.emailAgent      = emailAgent;
//         this.pdfAgent        = pdfAgent;
//         this.extractionAgent = extractionAgent;
//         this.excelAgent      = excelAgent;
//     }

//     public void run() throws Exception {

//         // Fetch raw messages directly (we need body + attachments)
//         List<Message> messages = emailAgent.fetchMessages();

//         for (Message message : messages) {
//             try {
//                 processMessage(message);
//             } catch (Exception e) {
//                 System.err.println("WorkflowService: error processing message — " + e.getMessage());
//             }
//         }
//     }

//     private void processMessage(Message message) throws Exception {

//         List<File> pdfs = emailAgent.extractPdfs(message);

//         if (!pdfs.isEmpty()) {
//             // ── Case 1: PDF(s) attached — extract from each PDF ──────────
//             for (File pdf : pdfs) {
//                 String text        = pdfAgent.extractText(pdf);
//                 ExtractedData data = extractionAgent.extract(text);
//                 excelAgent.append(pdf, data);
//             }

//         } else {
//             // ── Case 2: No PDF — extract fields from email body text ──────
//             String bodyText    = emailAgent.extractBodyText(message);
//             ExtractedData data = extractionAgent.extract(bodyText);
//             excelAgent.append(null, data);   // null = no PDF, leaves PDF column blank
//         }
//     }
// }

package com.company.aiagents.service;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final DocumentProcessingService emailPipeline;
    private final PortalWorkflowService portalPipeline;

    public WorkflowService(
            DocumentProcessingService emailPipeline,
            PortalWorkflowService portalPipeline ) {
        this.emailPipeline = emailPipeline;
        this.portalPipeline = portalPipeline;
    }

    public void run() throws Exception {
        System.out.println("WorkflowService: starting email pipeline...");
        emailPipeline.run();
    }
}