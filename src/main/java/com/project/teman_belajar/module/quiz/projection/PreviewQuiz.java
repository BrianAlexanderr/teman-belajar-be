package com.project.teman_belajar.module.quiz.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PreviewQuiz {
    UUID getId();
    String getTitle();
    LocalDateTime getCreatedAt();
}
