package com.project.teman_belajar.module.folder.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FolderRequest(
        @NotBlank
        String name
) {}
