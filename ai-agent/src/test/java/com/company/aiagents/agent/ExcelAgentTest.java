package com.company.aiagents.agent;

import com.company.aiagents.model.ExtractedData;
import com.company.aiagents.service.AddeparService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcelAgentTest {

    @Test
    void append_writesReportAndCallsAddepar(@TempDir Path tempDir) throws Exception {
        AddeparService addeparService = mock(AddeparService.class);
        ExcelAgent agent = new ExcelAgent(addeparService);
        File reportFile = tempDir.resolve("report.xlsx").toFile();
        setReportOutputPath(agent, reportFile.getAbsolutePath());

        ExtractedData data = createExtractedData();

        agent.append(data);

        assertTrue(reportFile.exists());
        try (Workbook workbook = WorkbookFactory.create(reportFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Fund Name", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Alpha Fund", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals(12345.0, sheet.getRow(1).getCell(3).getNumericCellValue());
        }

        verify(addeparService).importExcel(reportFile);
    }

    @Test
    void appendWithPdfFileName_includesPdfFileNameInReport(@TempDir Path tempDir) throws Exception {
        AddeparService addeparService = mock(AddeparService.class);
        ExcelAgent agent = new ExcelAgent(addeparService);
        File reportFile = tempDir.resolve("report.xlsx").toFile();
        setReportOutputPath(agent, reportFile.getAbsolutePath());

        File pdfFile = tempDir.resolve("statement.pdf").toFile();
        assertTrue(pdfFile.createNewFile());

        ExtractedData data = createExtractedData();

        agent.append(pdfFile, data);

        assertTrue(reportFile.exists());
        try (Workbook workbook = WorkbookFactory.create(reportFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("statement.pdf", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("Alpha Fund", sheet.getRow(1).getCell(1).getStringCellValue());
        }

        verify(addeparService).importExcel(reportFile);
    }

    private static ExtractedData createExtractedData() {
        ExtractedData data = new ExtractedData();
        data.setFundName("Alpha Fund");
        data.setManager("Manager A");
        data.setAmount("12345");
        data.setDueDate("2026-06-08");
        data.setDocumentType("Statement");
        data.setReferenceNumber("REF-001");
        return data;
    }

    private static void setReportOutputPath(ExcelAgent agent, String path) throws Exception {
        Field field = ExcelAgent.class.getDeclaredField("reportOutputPath");
        field.setAccessible(true);
        field.set(agent, path);
    }
}
