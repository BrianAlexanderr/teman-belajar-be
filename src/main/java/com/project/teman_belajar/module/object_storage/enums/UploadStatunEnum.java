package com.project.teman_belajar.module.object_storage.enums;

import lombok.Getter;

@Getter
public enum UploadStatunEnum {

    SUCCESS("success"),
    PENDING("pending");

    private final String label;

    UploadStatunEnum(String label) {
        this.label = label;
    }

}
