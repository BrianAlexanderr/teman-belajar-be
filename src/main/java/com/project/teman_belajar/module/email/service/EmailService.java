package com.project.teman_belajar.module.email.service;

import com.project.teman_belajar.module.email.dto.request.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(SendEmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.to());
            helper.setSubject("Your Password Reset OTP - Teman Belajar");

            // HTML Content for a better look
            String htmlContent = String.format(
                    "<div style='font-family: Arial, sans-serif; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>" +
                            "<h2>Password Reset Request</h2>" +
                            "<p>Use the following alphanumeric code to reset your password. This code is valid for 5 minutes.</p>" +
                            "<div style='font-size: 24px; font-weight: bold; color: #4A90E2; letter-spacing: 5px; padding: 10px; background: #f4f4f4; text-align: center;'>%s</div>" +
                            "<p>If you did not request this, please ignore this email.</p>" +
                            "</div>", request.otp());

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            // Log the error and handle it based on your project needs
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

}
