package com.JobPortal.SpringJobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.JobPortal.SpringJobportal.model.User;
import com.JobPortal.SpringJobportal.repository.UserRepository;
import com.JobPortal.SpringJobportal.service.MailService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private MailService mailService;

    // ================= REGISTER USER =================
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        User existing = repo.findByEmail(user.getEmail());

        if (existing != null) {
            return "Email already registered";
        }

        repo.save(user);

        // send welcome email
        mailService.sendMail(
                user.getEmail(),
                "Welcome to Job Portal",
                "Hello " + user.getName() + ", welcome to our Job Portal!"
        );

        return "User Registered Successfully";
    }

    // ================= LOGIN USER =================
    @PostMapping("/login")
    public User login(@RequestBody User user) {
        User existing = repo.findByEmail(user.getEmail());

        if (existing != null && existing.getPassword().equals(user.getPassword())) {
            return existing;
        }

        return null;
    }

    // ================= GET PROFILE =================
    @GetMapping("/profile/{email}")
    public User getProfile(@PathVariable String email) {
        return repo.findByEmail(email);
    }

    // ================= UPDATE ACCOUNT =================
    @PutMapping("/update/{email}")
    public String updateAccount(@PathVariable String email, @RequestBody User updated) {
        User existing = repo.findByEmail(email);
        if (existing == null) {
            return "User not found";
        }

        // Update fields (only if provided)
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getPassword() != null) existing.setPassword(updated.getPassword());

        repo.save(existing);
        return "Account updated successfully";
    }

    // ================= DELETE ACCOUNT =================
    @DeleteMapping("/delete/{email}")
    public String deleteAccount(@PathVariable String email) {
        User existing = repo.findByEmail(email);
        if (existing == null) {
            return "User not found";
        }

        repo.delete(existing);
        return "Account deleted successfully";
    }
}