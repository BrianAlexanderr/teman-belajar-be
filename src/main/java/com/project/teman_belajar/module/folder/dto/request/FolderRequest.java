package com.project.teman_belajar.module.folder.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public record FolderRequest(
        @NotBlank
        String name
) {}
