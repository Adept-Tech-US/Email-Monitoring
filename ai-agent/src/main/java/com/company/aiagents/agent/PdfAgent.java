package com.company.aiagents.agent;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class PdfAgent {
    public String extractText(File pdf)
            throws Exception {

        try (PDDocument document = Loader.loadPDF(pdf)) { //open pdf using Apache PDFBox
            PDFTextStripper stripper =
                    new PDFTextStripper(); // Reads all text from every page 

            return stripper.getText(document);
        }
    }
}
