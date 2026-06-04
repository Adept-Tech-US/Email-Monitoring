package com.company.aiagents.agent;

import com.company.aiagents.model.ExtractedData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
public class ExcelAgent {

    @Value("${report.output-path:D:\\Adept-Tech\\email-monitoring\\ai-agent\\output\\report.xlsx}")
    private String reportOutputPath;

    public void append(File pdf, ExtractedData data) throws Exception {
        writeRow(pdf, data);
    }

    public void append(ExtractedData data) throws Exception {
        writeRow(null, data);
    }

    private void writeRow(File pdf, ExtractedData data) throws Exception {
        File file = new File(reportOutputPath);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        Workbook workbook = openOrCreateWorkbook(file);
        Sheet sheet = workbook.getNumberOfSheets() == 0
                ? workbook.createSheet("Report")
                : workbook.getSheetAt(0);

        ensureHeader(workbook, sheet);

        int rowNum = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(pdf == null ? "" : pdf.getName());
        row.createCell(1).setCellValue(nullToBlank(data.getFundName()));
        row.createCell(2).setCellValue(nullToBlank(data.getManager()));

        Cell amountCell = row.createCell(3);
        if (data.getAmount() == null || data.getAmount().isBlank()) {
            amountCell.setCellValue("");
        } else {
            amountCell.setCellValue(data.getAmountAsDouble());
        }

        row.createCell(4).setCellValue(nullToBlank(data.getDueDate()));
        row.createCell(5).setCellValue(nullToBlank(data.getDocumentType()));
        row.createCell(6).setCellValue(nullToBlank(data.getReferenceNumber()));

        for (int i = 0; i <= 6; i++) {
            sheet.autoSizeColumn(i);
        }

        File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            workbook.write(out);
        } finally {
            workbook.close();
        }

        Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("ExcelAgent: row added -> " + file.getAbsolutePath());
    }

    private Workbook openOrCreateWorkbook(File file) throws IOException {
        if (!file.exists() || file.length() == 0) {
            return new XSSFWorkbook();
        }

        try {
            return WorkbookFactory.create(file);
        } catch (Exception e) {
            System.err.println("ExcelAgent: existing report is invalid; creating a new workbook. Reason: " + e.getMessage());
            return new XSSFWorkbook();
        }
    }

    private void ensureHeader(Workbook workbook, Sheet sheet) {
        if (sheet.getRow(0) != null) {
            return;
        }

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        String[] columns = {
                "PDF File",
                "Fund Name",
                "Manager",
                "Amount",
                "Due Date",
                "Document Type",
                "Reference Number"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
