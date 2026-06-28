package com.project.teman_belajar.module.upload.dto.response;

import lombok.Builder;

@Builder
public record StorageUrlResponse(
        String fileName,
        String url
) {}
