package com.project.teman_belajar.module.summarize.exception;

import com.project.teman_belajar.common.global_exception.dto.ErrorResponse;
import com.project.teman_belajar.module.summarize.exception.custom_exception.DocumentInvalidException;
import com.project.teman_belajar.module.summarize.exception.custom_exception.SummaryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.project.teman_belajar.module.summarize")
public class SummaryCustomException {

    @ExceptionHandler(SummaryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSummaryNotFound(SummaryNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DocumentInvalidException.class)
    public ResponseEntity<ErrorResponse> handleDocumentInvalid(DocumentInvalidException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
