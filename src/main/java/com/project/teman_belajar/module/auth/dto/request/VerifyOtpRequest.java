package com.project.teman_belajar.module.auth.dto.request;

public record VerifyOtpRequest(
        String email,
        String otp
) {
}
