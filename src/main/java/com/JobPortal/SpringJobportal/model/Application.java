package com.JobPortal.SpringJobportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String skills;
    private String resumeFileName;
    private String resumePath;
    private Long jobId;
    private LocalDateTime appliedAt = LocalDateTime.now();

    // New fields for status handling
    private String status = "Pending"; // Accepted / Rejected / Pending
    private Boolean locked = false; // true after status set to prevent changes

    // Default Constructor
    public Application() {}

    // Parameterized Constructor
    public Application(Long id, String name, String email, String phone, String skills,
                       String resumeFileName, String resumePath, Long jobId, LocalDateTime appliedAt,
                       String status, Boolean locked) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.resumeFileName = resumeFileName;
        this.resumePath = resumePath;
        this.jobId = jobId;
        this.appliedAt = appliedAt;
        this.status = status;
        this.locked = locked;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getLocked() { return locked; }
    public void setLocked(Boolean locked) { this.locked = locked; }
}