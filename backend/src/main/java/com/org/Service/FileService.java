package com.org.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {
    private final Path uploadDir = Paths.get("src/main/resources/uploads/");

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file");
        }
        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = originalFileName != null
            ? originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_").replaceAll("\\.+", ".")
            : "unknown.pdf";
        String fileName = UUID.randomUUID().toString() + "_" + sanitizedFileName;
        Path filePath = uploadDir.resolve(fileName).normalize();

        Files.copy(file.getInputStream(), filePath);
        return fileName;
    }

    public Path getFilePath(String fileName) {
        return uploadDir.resolve(fileName).normalize();
    }
}