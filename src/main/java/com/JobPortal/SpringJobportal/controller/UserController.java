package com.JobPortal.SpringJobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.JobPortal.SpringJobportal.model.User;
import com.JobPortal.SpringJobportal.repository.UserRepository;
import com.JobPortal.SpringJobportal.service.MailService;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private MailService mail;

    // REGISTER USER
    @PostMapping("/register")
    public String register(@RequestBody User user){

        User existing = repo.findByEmail(user.getEmail());

        if(existing != null){
            return "Email already registered";
        }

        repo.save(user);

        mail.sendMail(
                user.getEmail(),
                "Welcome to Job Portal",
                "Hello " + user.getName() + ", Welcome to our Job Portal"
        );

        return "User Registered Successfully";
    }


    // LOGIN USER
    @PostMapping("/login")
    public User login(@RequestBody User user){

        User existing = repo.findByEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        );

        if(existing != null){
            return existing;
        }

        return null;
    }


    // GET USER BY ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id){

        Optional<User> user = repo.findById(id);

        return user.orElse(null);
    }


    // UPDATE USER
    @PutMapping("/update/{id}")
    public User update(@PathVariable Long id, @RequestBody User updatedUser){

        Optional<User> existing = repo.findById(id);

        if(existing.isPresent()){

            User user = existing.get();

            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());

            return repo.save(user);
        }

        return null;
    }


    // DELETE USER
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id){

        repo.deleteById(id);

        return "User Deleted Successfully";
    }

}