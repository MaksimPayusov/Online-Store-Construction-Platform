package com.example.fileservice.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class fileConfig {
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.port}")
    private int minioPort;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Bean
    public MinioClient minioClient() {
        try {
            return MinioClient.builder()
                    .endpoint(minioEndpoint, minioPort, false)
                    .credentials(minioAccessKey, minioSecretKey)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Minio client", e);
        }
    }
}
