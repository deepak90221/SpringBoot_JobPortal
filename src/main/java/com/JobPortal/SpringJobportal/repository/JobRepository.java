package com.JobPortal.SpringJobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.JobPortal.SpringJobportal.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}