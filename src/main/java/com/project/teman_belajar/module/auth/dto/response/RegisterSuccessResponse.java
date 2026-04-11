package com.project.teman_belajar.module.auth.dto.response;

import java.util.Date;

public record RegisterSuccessResponse(
        String message,
        Date timeStamp
) {}
