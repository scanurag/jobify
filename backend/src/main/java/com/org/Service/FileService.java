package com.org.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {
  
    private final String uploadDir = "src/main/resources/uploads/";

    public String uploadFile(MultipartFile file) throws Exception {
      
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

  
        Files.createDirectories(filePath.getParent());

        Files.write(filePath, file.getBytes());


        return fileName;
    }

    public Path getFilePath(String fileName) {
        return Paths.get(uploadDir, fileName);
    }
}
