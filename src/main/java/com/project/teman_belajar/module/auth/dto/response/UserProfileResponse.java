package com.project.teman_belajar.module.auth.dto.response;

import lombok.Builder;

@Builder
public record UserProfileResponse(
        String userName,
        String email,
        Long jumlahAttemptedQuiz,
        Double averageScore
) {}
