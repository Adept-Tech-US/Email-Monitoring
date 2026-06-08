// package com.company.aiagents.service;

// import com.company.aiagents.agent.*;
// import com.company.aiagents.model.*;

// import com.microsoft.playwright.Page;

// import java.io.File;
// import java.util.List;

// public class PortalWorkflowService {

//     private final PortalAgent portalAgent =
//             new PortalAgent();

//     private final DownloadAgent downloadAgent =
//             new DownloadAgent();

//     public List<File> retrieveDocuments(
//             String portalUrl,
//             PortalCredential credential)
//             throws Exception {

//         Page page =
//                 portalAgent.connect(
//                         portalUrl,
//                         credential);

//         return downloadAgent.downloadFiles(
//                 page);
//     }
// }

package com.company.aiagents.service;

import com.company.aiagents.agent.*;
import com.company.aiagents.model.ExtractedData;
import com.company.aiagents.model.PortalCredential;
import com.company.aiagents.repository.ProcessedDocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class PortalWorkflowService {

    @Value("${portal.url:}")
    private String portalUrl;

    @Value("${portal.username:}")
    private String portalUsername;

    @Value("${portal.password:}")
    private String portalPassword;

    @Value("${portal.name:portal-email-link}")
    private String portalName;

    private final PortalAgent portalAgent;
    private final DownloadAgent downloadAgent;
    private final PdfAgent pdfAgent;
    private final ExtractionAgent extractionAgent;
    private final ExcelAgent excelAgent;
    private final S3StorageService s3;
    private final ProcessedDocumentRepository repo;

    public PortalWorkflowService(
            PortalAgent portalAgent, DownloadAgent downloadAgent,
            PdfAgent pdfAgent, ExtractionAgent extractionAgent,
            ExcelAgent excelAgent, S3StorageService s3,
            ProcessedDocumentRepository repo) {
        this.portalAgent = portalAgent;
        this.downloadAgent = downloadAgent;
        this.pdfAgent = pdfAgent;
        this.extractionAgent = extractionAgent;
        this.excelAgent = excelAgent;
        this.s3 = s3;
        this.repo = repo;
    }

    public void run(PortalCredential credential) throws Exception {
        if (portalUrl == null || portalUrl.isBlank()) {
            System.out.println("PortalWorkflowService: no portal URL configured, skipping.");
            return;
        }
        runInternal(portalUrl, credential);
    }

    public void runWithUrl(String portalUrlOverride) throws Exception {
        if (portalUrlOverride == null || portalUrlOverride.isBlank()) {
            System.out.println("PortalWorkflowService: no portal URL provided, skipping.");
            return;
        }
        if (!hasPortalCredentials()) {
            System.out.println("PortalWorkflowService: portal credentials not configured, skipping portal workflow for " + portalUrlOverride);
            return;
        }
        PortalCredential credential = buildCredential();
        runInternal(portalUrlOverride, credential);
    }

    private boolean hasPortalCredentials() {
        return portalUsername != null && !portalUsername.isBlank()
                && portalPassword != null && !portalPassword.isBlank();
    }

    private PortalCredential buildCredential() {
        PortalCredential credential = new PortalCredential();
        credential.setPortalName(portalName);
        credential.setUsername(portalUsername);
        credential.setPassword(portalPassword);
        return credential;
    }

    private void runInternal(String url, PortalCredential credential) throws Exception {
        try (PortalAgent.PlaywrightSession session = portalAgent.connect(url, credential)) {
            List<File> pdfs = downloadAgent.downloadFiles(session.page());

            for (File pdf : pdfs) {
                if (repo.alreadyProcessed(pdf.getName())) continue;
                s3.upload(pdf, "portal-pdfs/" + credential.getPortalName());
                String text = pdfAgent.extractText(pdf);
                ExtractedData data = extractionAgent.extract(text);
                excelAgent.append(pdf, data);
                repo.markProcessed(pdf.getName());
            }
        }
    }
}