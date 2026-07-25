package com.project.teman_belajar.module.object_storage.dto.response;

import lombok.Builder;

@Builder
public record StorageUrlResponse(
        String fileName,
        String url
) {}
