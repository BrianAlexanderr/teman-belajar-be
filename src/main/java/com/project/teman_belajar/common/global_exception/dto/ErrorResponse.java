package com.project.teman_belajar.common.global_exception.dto;

public record ErrorResponse(
        Integer statusCode,
        String msg,
        String timeStamp
) {}
