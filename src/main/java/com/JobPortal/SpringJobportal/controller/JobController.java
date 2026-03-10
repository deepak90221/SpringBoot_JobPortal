package com.JobPortal.SpringJobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.JobPortal.SpringJobportal.model.Job;
import com.JobPortal.SpringJobportal.repository.JobRepository;

@RestController
@RequestMapping("/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private JobRepository repo;

    // Add Job
    @PostMapping("/add")
    public ResponseEntity<Job> addJob(@RequestBody Job job){
        Job savedJob = repo.save(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedJob);
    }

    // Get all Jobs
    @GetMapping("/all")
    public ResponseEntity<List<Job>> getAllJobs(){
        List<Job> jobs = repo.findAll();
        return ResponseEntity.ok(jobs);
    }

    // Get single job
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Long id){
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Update Job
    @PutMapping("/update/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job updatedJob){
        return repo.findById(id)
                .map(job -> {
                    job.setTitle(updatedJob.getTitle());
                    job.setCompany(updatedJob.getCompany());
                    job.setLocation(updatedJob.getLocation());
                    job.setDescription(updatedJob.getDescription());
                    Job savedJob = repo.save(job);
                    return ResponseEntity.ok(savedJob);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Delete Job
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id){
        if(!repo.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");
        }
        repo.deleteById(id);
        return ResponseEntity.ok("Job Deleted Successfully");
    }
}