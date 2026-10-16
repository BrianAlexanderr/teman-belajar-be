package com.project.teman_belajar.module.quiz.dto.response;

import java.util.Date;

public record QuizSuccessResponse (
        String message,
        Integer score,
        Date timeStamp
) {}
