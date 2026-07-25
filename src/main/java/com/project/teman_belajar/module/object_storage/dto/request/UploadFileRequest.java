package com.project.teman_belajar.module.object_storage.dto.request;

public record UploadFileRequest(
        String folderId,
        String fileName,
        String fileType
) {}
