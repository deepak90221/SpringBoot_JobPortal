package com.JobPortal.SpringJobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AdminMailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an email to the applicant when their application is accepted or rejected.
     *
     * @param to       Applicant email
     * @param name     Applicant name
     * @param jobTitle Job title applied for
     * @param status   "Accepted" or "Rejected"
     */
    public void sendStatusMail(String to, String name, String jobTitle, String status) {
        try {
            String subject = "Update on Your Job Application for \"" + jobTitle + "\"";
            String message;

            if ("Accepted".equalsIgnoreCase(status)) {
                message = "Hello " + name + ",\n\n"
                        + "Congratulations! We are pleased to inform you that your application for \"" + jobTitle + "\" has been ACCEPTED.\n"
                        + "Our team will contact you with the next steps shortly.\n\n"
                        + "Best Regards,\nJob Portal Team";
            } else if ("Rejected".equalsIgnoreCase(status)) {
                message = "Hello " + name + ",\n\n"
                        + "We appreciate your interest in \"" + jobTitle + "\". "
                        + "After careful consideration, we regret to inform you that your application has been REJECTED.\n"
                        + "We encourage you to apply for other opportunities in the future.\n\n"
                        + "Best Regards,\nJob Portal Team";
            } else {
                message = "Hello " + name + ",\n\n"
                        + "Your application status for \"" + jobTitle + "\" has been updated to: " + status + ".\n\n"
                        + "Best Regards,\nJob Portal Team";
            }

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            System.out.println("Status email sent to: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send email to " + to);
        }
    }
}