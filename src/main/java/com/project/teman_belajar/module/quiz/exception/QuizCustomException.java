package com.project.teman_belajar.module.quiz.exception;

import com.project.teman_belajar.common.global_exception.dto.ErrorResponse;
import com.project.teman_belajar.module.quiz.exception.custom_exception.AnswerNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.project.teman_belajar.module.materials")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class QuizCustomException {

    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAnswerNotFound(AnswerNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
