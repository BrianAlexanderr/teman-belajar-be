package com.project.teman_belajar.module.email.dto.request;

public record SendEmailRequest(
        String to,
        String body,
        String title,
        String otp
) {}
