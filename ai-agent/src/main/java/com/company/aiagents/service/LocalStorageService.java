package com.company.aiagents.service;

import com.company.aiagents.model.ExtractedData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class LocalStorageService {

    @Value("${storage.local.base-path:output/clients}")
    private String basePath;

    public File save(File sourceFile, ExtractedData data) throws IOException {
        String relativeFolder = buildRelativePath(data);
        File targetDir = new File(basePath, relativeFolder);
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IOException("LocalStorageService: failed to create directory " + targetDir.getAbsolutePath());
        }

        File destination = new File(targetDir, sourceFile.getName());
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("LocalStorageService: saved file to " + destination.getAbsolutePath());
        return destination;
    }

    public String buildRelativePath(ExtractedData data) {
        String fundName = sanitize(data.getFundName());
        String documentType = sanitize(data.getDocumentType());
        return fundName + "/" + documentType;
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        String sanitized = value.trim().replaceAll("[\\\\/:*?\"<>|]", "_");
        sanitized = sanitized.replaceAll("\\s{2,}", " ");
        return sanitized;
    }
}
