package com.project.teman_belajar.module.upload.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AllowedMaterialFileType {
    // Images: JPG, PNG, WEBP
    IMAGE(List.of("image/jpeg", "image/png", "image/webp")),

    // Documents: PDF, DOC, DOCX
    DOCUMENT(List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )),

    // PowerPoint: PPT, PPTX
    PPT(List.of(
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    ));

    private final List<String> mimeTypes;

    AllowedMaterialFileType(List<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public static boolean isValidContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }

        String cleanContentType = contentType.trim().toLowerCase();

        return Arrays.stream(values())
                .anyMatch(type -> type.getMimeTypes().contains(cleanContentType));
    }
}
