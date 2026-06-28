package com.project.teman_belajar.module.upload.dto.request;

public record UploadFileRequest(
        String folderId,
        String fileName,
        String fileType
) {}
