package com.nexushr.payroll.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class S3StorageService {

    // For demo purposes, we mock S3 storage in memory
    // In production, this uses software.amazon.awssdk.services.s3.S3Client
    private final ConcurrentHashMap<String, byte[]> mockS3Bucket = new ConcurrentHashMap<>();

    public String uploadPayslip(String employeeId, String period, byte[] pdfBytes) {
        String objectKey = "payslips/" + period + "/" + employeeId + "_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
        mockS3Bucket.put(objectKey, pdfBytes);
        log.info("Mock S3 Upload: Stored {} bytes at {}", pdfBytes.length, objectKey);
        
        // Return a mock pre-signed URL (in real app: s3Presigner.presignGetObject(...))
        return "https://mock-s3.nexushr.local/download/" + objectKey;
    }

    public byte[] downloadMockFile(String objectKey) {
        return mockS3Bucket.get(objectKey);
    }
}
