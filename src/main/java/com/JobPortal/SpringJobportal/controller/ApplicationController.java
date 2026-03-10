package com.JobPortal.SpringJobportal.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.JobPortal.SpringJobportal.model.Application;
import com.JobPortal.SpringJobportal.model.Job;
import com.JobPortal.SpringJobportal.repository.ApplicationRepository;
import com.JobPortal.SpringJobportal.repository.JobRepository;
import com.JobPortal.SpringJobportal.service.AdminMailService;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationRepository repo;

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private AdminMailService mailService;

    // ================= UPDATE APPLICATION STATUS =================
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> req) {
        try {
            String status = req.get("status");
            if (status == null || (!status.equals("Accepted") && !status.equals("Rejected"))) {
                return ResponseEntity.badRequest().body("Invalid status");
            }

            Application app = repo.findById(id).orElse(null);
            if (app == null) return ResponseEntity.status(404).body("Application not found");

            if (Boolean.TRUE.equals(app.getLocked())) {
                return ResponseEntity.status(400).body("Application status already locked");
            }

            app.setStatus(status);
            app.setLocked(true);
            repo.save(app);

            // Optional: send email notification
            Optional<Job> jobOpt = jobRepo.findById(app.getJobId());
            String jobTitle = jobOpt.map(Job::getTitle).orElse("your applied job");
            mailService.sendStatusMail(app.getEmail(), app.getName(), jobTitle, status);

            return ResponseEntity.ok("Application " + status + " and email sent");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update status: " + e.getMessage());
        }
    }
    
    // ================= GET ALL APPLICATIONS FOR ADMIN =================
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllApplicationsWithJobs() {
        try {
            List<Application> apps = repo.findAll();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Application app : apps) {
                Map<String, Object> map = new HashMap<>();
                map.put("appId", app.getId());
                map.put("appliedAt", app.getAppliedAt());
                map.put("name", app.getName());
                map.put("email", app.getEmail());
                map.put("phone", app.getPhone());
                map.put("skills", app.getSkills());
                map.put("status", app.getStatus());
                map.put("locked", app.getLocked());

                if (app.getJobId() != null) {
                    jobRepo.findById(app.getJobId()).ifPresent(job -> {
                        map.put("jobTitle", job.getTitle());
                        map.put("company", job.getCompany());
                        map.put("location", job.getLocation());
                    });
                }

                result.add(map);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to fetch applications: " + e.getMessage());
        }
    }
    @GetMapping
    public List<Application> getAllApplications(){
        return repo.findAll();
    }

    // ================= APPLY JOB =================
    @PostMapping("/apply")
    public ResponseEntity<?> applyJob(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String skills,
            @RequestParam Long jobId,
            @RequestParam MultipartFile file
    ) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/resumes/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + filename;

            file.transferTo(new File(filePath));

            Application app = new Application();
            app.setName(name);
            app.setEmail(email);
            app.setPhone(phone);
            app.setSkills(skills);
            app.setResumeFileName(filename);
            app.setResumePath(filePath);
            app.setJobId(jobId);

            repo.save(app);

            return ResponseEntity.ok("Application Submitted Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to submit application: " + e.getMessage());
        }
    }

    // ================= DELETE APPLICATION =================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            if (!repo.existsById(id)) return ResponseEntity.status(404).body("Application not found");
            repo.deleteById(id);
            return ResponseEntity.ok("Application Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete application: " + e.getMessage());
        }
    }

    // ================= UPDATE APPLICATION DETAILS =================
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateApplication(@PathVariable Long id, @RequestBody Application updated) {
        try {
            Application app = repo.findById(id).orElse(null);
            if (app == null) return ResponseEntity.status(404).body("Application not found");

            app.setName(updated.getName());
            app.setPhone(updated.getPhone());
            app.setSkills(updated.getSkills());

            repo.save(app);
            return ResponseEntity.ok(app);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update application: " + e.getMessage());
        }
    }

    // ================= DOWNLOAD RESUME =================
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadResume(@PathVariable Long id) {
        try {
            Optional<Application> optionalApp = repo.findById(id);
            if (!optionalApp.isPresent()) return ResponseEntity.status(404).body("Application not found");

            Application app = optionalApp.get();
            Path path = Paths.get(app.getResumePath());
            if (!path.toFile().exists()) return ResponseEntity.status(404).body("Resume file not found");

            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to download resume: " + e.getMessage());
        }
    }

    // ================= VIEW RESUME INLINE =================
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewResume(@PathVariable Long id) {
        try {
            Optional<Application> optionalApp = repo.findById(id);
            if (!optionalApp.isPresent()) return ResponseEntity.status(404).body("Application not found");

            Application app = optionalApp.get();
            Path path = Paths.get(app.getResumePath());
            if (!path.toFile().exists()) return ResponseEntity.status(404).body("Resume file not found");

            Resource resource = new UrlResource(path.toUri());

            String contentType = Files.probeContentType(path);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to view resume: " + e.getMessage());
        }
    }

    // ================= GET USER APPLICATIONS =================
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserApplications(@PathVariable String email) {
        try {
            List<Application> apps = repo.findByEmail(email);
            return ResponseEntity.ok(apps);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to fetch user applications");
        }
    }

    @GetMapping("/user/jobs/{email}")
    public ResponseEntity<?> getUserApplicationsWithJobs(@PathVariable String email) {
        try {

            List<Application> apps = repo.findByEmail(email);
            List<Map<String, Object>> result = new ArrayList<>();

            for (Application app : apps) {

                Map<String, Object> map = new HashMap<>();

                map.put("appId", app.getId());
                map.put("appliedAt", app.getAppliedAt());
                map.put("name", app.getName());
                map.put("phone", app.getPhone());
                map.put("skills", app.getSkills());
                map.put("status", app.getStatus());   // IMPORTANT
                map.put("email", app.getEmail());

                if (app.getJobId() != null) {

                    jobRepo.findById(app.getJobId()).ifPresent(job -> {
                        map.put("jobTitle", job.getTitle());
                        map.put("company", job.getCompany());
                        map.put("location", job.getLocation());
                    });

                }

                result.add(map);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Failed to fetch user applications: " + e.getMessage());
        }
    }
}