package com.JobPortal.SpringJobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.JobPortal.SpringJobportal.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

}