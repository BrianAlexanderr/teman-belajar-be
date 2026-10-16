package com.project.teman_belajar.module.auth.dto.request;

import lombok.Builder;

@Builder
public record UpdateUserProfileReuqest(
        String name,
        String email
) {}
