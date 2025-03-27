package com.example.socialback.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")       
public class FileController {

    private final Path uploadDir = Paths.get("C:\\\\uploads\\\\profile-pictures");

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // ตั้งชื่อไฟล์ใหม่ให้ไม่ซ้ำกัน
            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path targetLocation = uploadDir.resolve(filename);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "http://localhost:8080/files/profile-pictures/" + filename;
            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "File upload failed"));
        }
    }

    @GetMapping("/profile-pictures/{filename:.+}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();

            // ตรวจสอบว่าไฟล์มีอยู่จริง
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
