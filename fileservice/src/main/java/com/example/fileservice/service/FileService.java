package com.example.fileservice.service;

import com.example.fileservice.dto.response.FileInfoDto;
import com.example.fileservice.dto.response.FileUploadResponseDto;
import com.example.fileservice.exception.FileNotFoundException;
import com.example.fileservice.exception.FileStorageException;
import com.example.fileservice.exception.InvalidFileException;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.public-url}")
    private String publicUrl;

    @Value("${file.max-size:10485760}") // 10MB default
    private long maxFileSize;

    @Value("${file.allowed-extensions}")
    private String allowedExtensions;

    public FileUploadResponseDto uploadFile(MultipartFile file) {
        log.info("Starting file upload: {}", file.getOriginalFilename());

        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String objectName = UUID.randomUUID().toString() + fileExtension;

        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            minioClient.putObject(args);

            String fileUrl = String.format("%s/%s/%s", publicUrl, bucketName, objectName);

            log.info("File uploaded successfully: {} -> {}", originalFilename, objectName);

            return FileUploadResponseDto.builder()
                    .fileUrl(fileUrl)
                    .fileName(objectName)
                    .originalFileName(originalFilename)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .uploadedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Failed to upload file: {}", originalFilename, e);
            throw new FileStorageException("Failed to upload file: " + originalFilename, e);
        }
    }

    public InputStream getFile(String fileName) {
        log.info("Retrieving file: {}", fileName);

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to retrieve file: {}", fileName, e);
            throw new FileNotFoundException("File not found: " + fileName);
        }
    }

    public void deleteFile(String fileName) {
        log.info("Deleting file: {}", fileName);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            log.info("File deleted successfully: {}", fileName);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileName, e);
            throw new FileStorageException("Failed to delete file: " + fileName, e);
        }
    }

    public List<FileInfoDto> listFiles() {
        log.info("Listing all files in bucket: {}", bucketName);

        List<FileInfoDto> files = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                String fileUrl = String.format("%s/%s/%s", publicUrl, bucketName, item.objectName());

                files.add(FileInfoDto.builder()
                        .fileName(item.objectName())
                        .size(item.size())
                        .contentType("application/octet-stream")
                        .url(fileUrl)
                        .build());
            }

            log.info("Found {} files", files.size());
            return files;

        } catch (Exception e) {
            log.error("Failed to list files", e);
            throw new FileStorageException("Failed to list files", e);
        }
    }

    public boolean fileExists(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new InvalidFileException(
                    String.format("File size exceeds maximum allowed size of %d bytes", maxFileSize)
            );
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidFileException("File name is invalid");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        Set<String> allowedExtensionsSet = new HashSet<>(Arrays.asList(allowedExtensions.split(",")));

        if (!allowedExtensionsSet.contains(extension)) {
            throw new InvalidFileException(
                    String.format("File extension '%s' is not allowed. Allowed: %s", 
                            extension, allowedExtensions)
            );
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}