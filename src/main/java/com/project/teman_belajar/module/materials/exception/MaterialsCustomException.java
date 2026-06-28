package com.project.teman_belajar.module.materials.exception;

import com.project.teman_belajar.common.global_exception.dto.ErrorResponse;
import com.project.teman_belajar.module.materials.exception.custom_exception.FileNotFoundException;
import com.project.teman_belajar.module.materials.exception.custom_exception.FileTypeNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.project.teman_belajar.module.materials")
public class MaterialsCustomException {

    @ExceptionHandler(FileTypeNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleFileTypeNotValid(FileTypeNotAllowedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(FileNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
