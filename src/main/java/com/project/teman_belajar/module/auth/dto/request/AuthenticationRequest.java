package com.project.teman_belajar.module.auth.dto.request;

public record AuthenticationRequest(
        String email,
        String password
) {}
