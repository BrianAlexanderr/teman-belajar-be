package com.project.teman_belajar.module.upload.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UploadStatunEnum {

    SUCCESS("success"),
    PENDING("pending");

    private final String label;

    UploadStatunEnum(String label) {
        this.label = label;
    }

    public static UploadStatunEnum getEnum(String label) {
        return Arrays.stream(UploadStatunEnum.values())
                .filter(e -> e.label.equals(label))
                .findFirst()
                .orElse(null);
    }

}
