package com.company.aiagents.service;

import com.company.aiagents.model.ExtractedData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocalStorageServiceTest {

    @Test
    void save_copiesFileIntoSanitizedRelativePath(@TempDir Path tempDir) throws Exception {
        LocalStorageService service = new LocalStorageService();
        setBasePath(service, tempDir.toFile().getAbsolutePath());

        File source = tempDir.resolve("statement.pdf").toFile();
        try (FileWriter writer = new FileWriter(source)) {
            writer.write("dummy content");
        }

        ExtractedData data = new ExtractedData();
        data.setFundName("Alpha / Fund");
        data.setDocumentType("Distribution: Statement");

        File saved = service.save(source, data);

        assertTrue(saved.exists());
        assertEquals("statement.pdf", saved.getName());
        assertEquals(tempDir.resolve("Alpha _ Fund/Distribution_ Statement/statement.pdf").toFile().getAbsolutePath(), saved.getAbsolutePath());
        assertTrue(saved.length() > 0);
    }

    @Test
    void buildRelativePath_returnsUnknownForMissingFields() throws Exception {
        LocalStorageService service = new LocalStorageService();
        ExtractedData data = new ExtractedData();
        data.setFundName(null);
        data.setDocumentType("   ");

        assertEquals("unknown/unknown", service.buildRelativePath(data));
    }

    private static void setBasePath(LocalStorageService service, String basePath) throws Exception {
        Field field = LocalStorageService.class.getDeclaredField("basePath");
        field.setAccessible(true);
        field.set(service, basePath);
    }
}
