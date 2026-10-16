package com.project.teman_belajar.module.quiz.exception.custom_exception;

public class AnswerNotFoundException extends RuntimeException {
    public AnswerNotFoundException(String message) {
        super(message);
    }
}
