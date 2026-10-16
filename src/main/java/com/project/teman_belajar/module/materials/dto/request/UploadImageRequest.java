package com.project.teman_belajar.module.materials.dto.request;

public record UploadImageRequest(
   String extractedText,
   String fileName,
   String fileType,
   String folderId
) {}
