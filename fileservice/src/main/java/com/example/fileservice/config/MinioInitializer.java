package com.example.fileservice.config;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioInitializer {

    private final MinioClient minioClient;
    
    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void init() {
        setupBucketAndPolicy();
    }

    public void setupBucketAndPolicy() {
        try {
            boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );

            if (!found) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                );
                System.out.println("[MinIO] Bucket '" + bucketName + "' created successfully");
            } else {
                System.out.println("[MinIO] Bucket '" + bucketName + "' already exists");
            }

            String publicReadPolicy = String.format(
                    "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":\"*\","
                            + "\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::%s/*\"]}]}",
                    bucketName
            );

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(publicReadPolicy)
                            .build()
            );
            
            System.out.println("[MinIO] Public read policy applied to bucket '" + bucketName + "'");

        } catch (Exception e) {
            System.err.println("[MinIO] Setup failed: " + e.getMessage());
            throw new RuntimeException("MinIO setup failed", e);
        }
    }
}