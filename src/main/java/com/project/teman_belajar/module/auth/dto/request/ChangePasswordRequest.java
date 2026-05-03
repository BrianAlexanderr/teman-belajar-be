package com.project.teman_belajar.module.auth.dto.request;

public record ChangePasswordRequest(
        String email,
        String newPassword,
        String otp
) {}
