package za.ac.cput.logisticmanagementsystem.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String recipientEmail,
            String recipientName,
            String token) {

        String resetLink = frontendUrl
                + "/reset-password?token="
                + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Logistics Management System - Password Reset");

        message.setText(
                "Hello " + recipientName + ",\n\n"
                        + "Your password-reset request has been approved.\n\n"
                        + "Click the link below to set your new password:\n"
                        + resetLink + "\n\n"
                        + "This link expires after 24 hours and can only be used once.\n\n"
                        + "If you did not request a password reset, please contact your administrator.\n\n"
                        + "Logistics Management System"
        );

        mailSender.send(message);
    }
}

