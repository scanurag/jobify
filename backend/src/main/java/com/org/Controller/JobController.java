package com.org.Controller;

import com.org.Entity.Job;
import com.org.Entity.User;
import com.org.Repo.JobRepository;
import com.org.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/view")
    public List<Job> getJobs(@RequestParam(required = false) String location,
                             @RequestParam(required = false) String jobType,
                             @RequestParam(required = false) Long salary) {
        if (location != null) return jobRepository.findByLocationContainingIgnoreCase(location);
        if (jobType != null) return jobRepository.findByJobType(jobType);
        return jobRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobRepository.findById(id).orElseThrow());
    } 
    
    @PostMapping
    public ResponseEntity<Job> postJob(@RequestBody Job job, Principal principal) {
        System.out.println("Principal: " + principal);
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User hr = userRepository.findByEmail(principal.getName());
        if (hr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        job.setPostedBy(hr);
        Job saved = jobRepository.save(job);
        System.out.println("Saved Job: " + saved);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        jobRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
