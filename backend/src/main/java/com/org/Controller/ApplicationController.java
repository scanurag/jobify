package com.org.Controller;

import com.org.Entity.Application;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.org.Entity.Job;
import com.org.Entity.User;
import com.org.Repo.*;
import com.org.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api")
public class ApplicationController {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @PostMapping("/apply")
    public ResponseEntity<?> apply(
        @RequestParam("jobId") Long jobId,
        @RequestParam("email") String email,
        @RequestParam("fullName") String fullName,
        @RequestParam("phone") String phone,
        @RequestParam("resume") MultipartFile resume
    ) throws Exception {

        Job job = jobRepository.findById(jobId).orElse(null);
        User user = userRepository.findByEmail(email);

        if (job == null || user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid job or user"));
        }
        String resumeFileName = fileService.uploadFile(resume); 

        Application application = new Application();
        application.setJob(job);
        application.setUser(user);
        application.setApplyDate(LocalDate.now());
        application.setStatus("APPLIED");
        application.setResumeUrl(resumeFileName);  
        application.setCoverLetter("Applied by: " + fullName + ", Phone: " + phone);

        applicationRepository.save(application);

        return ResponseEntity.ok(Map.of("message", "Application submitted successfully"));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("resume") MultipartFile file) throws Exception {
        String url = fileService.uploadFile(file);
        return ResponseEntity.ok(Map.of("url", url));
    } 
    @GetMapping("/resume/download/{filename:.+}")
    public ResponseEntity<Resource> downloadResume(@PathVariable String filename) {
        try {
            logger.info("Attempting to download file: {}", filename);
            Path filePath = fileService.getFilePath(filename);
            logger.info("Resolved path: {}, exists: {}", filePath, Files.exists(filePath));
            if (!Files.exists(filePath)) {
                logger.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        } catch (MalformedURLException e) {
            logger.error("Error downloading file: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/crm/applications/hr")
    public List<Application> getApplicationsByHr(@RequestParam String email) {
        return applicationRepository.findByJobPostedByEmail(email);
    }


    @PutMapping("/crm/applications/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Application application = applicationRepository.findById(id).orElse(null);
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        application.setStatus(body.get("status"));
        applicationRepository.save(application);
        return ResponseEntity.ok(Map.of("message", "Status updated"));
    }
}
