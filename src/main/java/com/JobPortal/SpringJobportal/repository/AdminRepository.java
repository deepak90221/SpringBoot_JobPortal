package com.JobPortal.SpringJobportal.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.JobPortal.SpringJobportal.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByEmail(String email);
}