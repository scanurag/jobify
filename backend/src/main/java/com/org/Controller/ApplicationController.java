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

@RestController
@RequestMapping("/api")
public class ApplicationController {

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
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws Exception {
        Path path = fileService.getFilePath(fileName);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + fileName)
            .body(resource);
    }

    @GetMapping("/resume/download/{filename:.+}")
    public ResponseEntity<Resource> downloadResume(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("src/main/resources/static/uploads/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

        } catch (MalformedURLException e) {
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
