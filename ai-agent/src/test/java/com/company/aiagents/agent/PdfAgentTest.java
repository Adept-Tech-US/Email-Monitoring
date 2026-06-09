package com.company.aiagents.agent;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PdfAgentTest {

    @Test
    void extractText_readsTextFromPdf(@TempDir Path tempDir) throws Exception {
        File pdfFile = tempDir.resolve("sample.pdf").toFile();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Fund Name: Test Fund");
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Amount Due: $1,234.56");
                contentStream.endText();
            }
            document.save(pdfFile);
        }

        PdfAgent agent = new PdfAgent();
        String text = agent.extractText(pdfFile);

        assertTrue(text.contains("Fund Name: Test Fund"));
        assertTrue(text.contains("Amount Due: $1,234.56"));
    }
}
