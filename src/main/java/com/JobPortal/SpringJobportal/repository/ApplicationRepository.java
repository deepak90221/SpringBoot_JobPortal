package com.JobPortal.SpringJobportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.JobPortal.SpringJobportal.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByEmail(String email);

}