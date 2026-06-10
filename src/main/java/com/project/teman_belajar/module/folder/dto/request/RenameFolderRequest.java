package com.project.teman_belajar.module.folder.dto.request;

import java.util.UUID;

public record RenameFolderRequest(
        UUID id,
        String newName
) {}
