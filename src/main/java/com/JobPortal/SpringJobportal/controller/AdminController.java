package com.JobPortal.SpringJobportal.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.JobPortal.SpringJobportal.model.Admin;
import com.JobPortal.SpringJobportal.repository.AdminRepository;
import com.JobPortal.SpringJobportal.service.MailService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private AdminRepository repo;

    @Autowired
    private MailService mailService;

    // Register Admin
    @PostMapping("/register")
    public String register(@RequestBody Admin admin){

        if(repo.findByEmail(admin.getEmail()) != null){
            return "Email already registered";
        }

        repo.save(admin);

        mailService.sendMail(
                admin.getEmail(),
                "Welcome Admin",
                "Hello " + admin.getName() + ", Welcome to Job Portal Admin Panel"
        );

        return "Admin Registered Successfully";
    }

    // Login Admin
    @PostMapping("/login")
    public Admin login(@RequestBody Admin admin){

        Admin existing = repo.findByEmail(admin.getEmail());

        if(existing != null && existing.getPassword().equals(admin.getPassword())){
            return existing;
        }

        return null;
    }

    // Admin profile
    @GetMapping("/profile/{email}")
    public Admin getProfile(@PathVariable String email){
        return repo.findByEmail(email);
    }
    
    @PutMapping("/update/{id}")
    public Admin updateAdmin(@PathVariable Long id, @RequestBody Admin adminDetails){
        Admin admin = repo.findById(id).orElse(null);

        if(admin != null){
            admin.setName(adminDetails.getName());
            admin.setPassword(adminDetails.getPassword());
            // Email should not change
            return repo.save(admin);
        }
        return null;
    }
}