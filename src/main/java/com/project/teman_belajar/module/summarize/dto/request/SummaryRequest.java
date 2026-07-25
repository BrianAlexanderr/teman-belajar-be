package com.project.teman_belajar.module.summarize.dto.request;

import java.util.List;
import java.util.UUID;

public record SummaryRequest(
        List<UUID> materialIds,
        UUID folderId
) {}
