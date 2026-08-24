package com.tracker.jobtracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tracker.jobtracker.model.Job;
import com.tracker.jobtracker.repository.JobRepository;
import com.tracker.jobtracker.service.ResumeService;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired(required = false)
    private ResumeService resumeService;

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Handles Multipart Form Data (Form + Resume Upload)
    @PostMapping("/upload")
    public Job createJobWithResume(
            @RequestParam("company") String company,
            @RequestParam("role") String role,
            @RequestParam("location") String location,
            @RequestParam("salary") String salary,
            @RequestParam("status") String status,
            @RequestParam(value = "resume", required = false) MultipartFile file) {

        Job job = new Job();
        job.setCompany(company);
        job.setRole(role);
        job.setLocation(location);
        job.setSalary(salary);
        job.setStatus(status);

        if (file != null && !file.isEmpty() && resumeService != null) {
            try {
                String extractedText = resumeService.extractText(file);
                int score = resumeService.calculateMatchScore(extractedText, role);
                
                job.setResumeText(extractedText);
                job.setResumeFileName(file.getOriginalFilename());
                job.setMatchScore(score);
            } catch (Exception e) {
                job.setResumeFileName(file.getOriginalFilename());
                job.setMatchScore(0);
            }
        }

        return jobRepository.save(job);
    }

    // Handles Standard JSON Payload (Fallback)
    @PostMapping
    public Job createJob(@RequestBody Job job) {
        return jobRepository.save(job);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job jobDetails) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setCompany(jobDetails.getCompany());
                    job.setRole(jobDetails.getRole());
                    job.setLocation(jobDetails.getLocation());
                    job.setSalary(jobDetails.getSalary());
                    job.setStatus(jobDetails.getStatus());
                    Job updatedJob = jobRepository.save(job);
                    return ResponseEntity.ok(updatedJob);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    jobRepository.delete(job);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}