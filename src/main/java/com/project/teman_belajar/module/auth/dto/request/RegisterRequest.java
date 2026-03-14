package com.project.teman_belajar.module.auth.dto.request;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {}
