package com.example.fileservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String minioHost; // Для формирования URL в браузере

    @Value("${minio.port}")
    private String minioPort; // Для формирования URL в браузере

    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file.");
        }

        // Генерируем уникальное имя файла
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString() + fileExtension;

        try {
            // 1. Создание параметров для загрузки
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1) // -1 для размера части, чтобы MinIO сам определил его
                    .contentType(file.getContentType())
                    .build();

            // 2. Загрузка файла
            minioClient.putObject(args);

            // 3. Формирование публичного URL для доступа (через порт MinIO Console/API 9000)
            // Важно: в реальном prod-приложении вы, вероятно, будете использовать
            // API Gateway (KrakenD) для проксирования MinIO,
            // но для тестирования используем прямой доступ через minio:9000

            // Замена "minio" на "localhost" для доступа извне контейнера
            // Примечание: внутри контейнеров URL будет "http://minio:9000/bucketName/objectName"
            // Снаружи: "http://localhost:9000/bucketName/objectName" (если 9000 порт проброшен)

            String publicUrl = String.format("http://%s:%s/%s/%s",
                    "localhost",
                    minioPort,
                    bucketName,
                    objectName
            );

            return publicUrl;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO: " + e.getMessage(), e);
        }
    }
}