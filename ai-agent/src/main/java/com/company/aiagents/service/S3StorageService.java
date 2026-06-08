package com.company.aiagents.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.nio.file.Path;

@Service
public class S3StorageService {

    @Value("${aws.s3.bucket}")
    private String bucket;

    private final S3Client s3;

    public S3StorageService(S3Client s3) {
        this.s3 = s3;
    }

// ── Runs automatically when Spring starts the app ──────────────────────
// to test the S3 connection    
@PostConstruct                                      
    public void verifyConnection() {
        try {
            s3.listBuckets();
            System.out.println("S3StorageService: connected to AWS S3 successfully.");
        } catch (Exception e) {
            System.err.println("S3StorageService: S3 connection failed — " + e.getMessage());
        }
    }


    /**
     * Uploads a local file to S3 under clientFolder/fileName.
     * Returns the S3 key so you can store it in your report.
     */
    public String upload(File file, String clientFolder) {
        String key = clientFolder + "/" + file.getName();
        s3.putObject(
            PutObjectRequest.builder().bucket(bucket).key(key).build(),
            Path.of(file.getAbsolutePath())
        );
        System.out.println("S3StorageService: uploaded s3://" + bucket + "/" + key);
        return key;
    }
}