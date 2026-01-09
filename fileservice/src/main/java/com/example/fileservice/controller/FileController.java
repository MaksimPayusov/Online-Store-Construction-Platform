package com.example.fileservice.controller;

import com.example.fileservice.dto.response.FileInfoDto;
import com.example.fileservice.dto.response.FileUploadResponseDto;
import com.example.fileservice.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(
        @RequestParam("file") MultipartFile file
    ) {
        log.info("Upload request received for file: {}", file.getOriginalFilename());
        FileUploadResponseDto response = fileService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        log.info("Download request received for file: {}", fileName);
        InputStream fileStream = fileService.getFile(fileName);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(fileStream));
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        log.info("Delete request received for file: {}", fileName);
        fileService.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FileInfoDto>> listFiles() {
        log.info("List files request received");
        List<FileInfoDto> files = fileService.listFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/exists/{fileName}")
    public ResponseEntity<Boolean> fileExists(@PathVariable String fileName) {
        boolean exists = fileService.fileExists(fileName);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/url")
    public ResponseEntity<String> getFileUrl(@RequestParam String fileName) {
        if (fileService.fileExists(fileName)) {
            return ResponseEntity.ok(fileService.uploadFile(null).getFileUrl());
        }
        return ResponseEntity.notFound().build();
    }
}