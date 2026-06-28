package com.project.teman_belajar.module.materials.dto.response;

import lombok.Builder;

@Builder
public record MaterialDetailResponse(
        String fileId,
        String fileName,
        String fileType
) {}
