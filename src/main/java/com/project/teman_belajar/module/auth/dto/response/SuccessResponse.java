package com.project.teman_belajar.module.auth.dto.response;

import java.util.Date;

public record SuccessResponse(
        String message,
        Date timeStamp
) {}
