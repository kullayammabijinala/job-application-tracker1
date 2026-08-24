package com.tracker.jobtracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    private String location;
    private String salary;
    private String status;
    private LocalDate appliedDate;

    // Resume Parsing & Analysis Fields
    @Lob
    @Column(columnDefinition = "CLOB")
    private String resumeText;
    
    private String resumeFileName;
    private int matchScore;

    public Job() {}

    public Job(String company, String role, String location, String salary, String status, LocalDate appliedDate) {
        this.company = company;
        this.role = role;
        this.location = location;
        this.salary = salary;
        this.status = status;
        this.appliedDate = appliedDate;
    }

    @PrePersist
    public void onCreate() {
        if (this.appliedDate == null) {
            this.appliedDate = LocalDate.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }
}