package com.project.teman_belajar.common.global_exception;

import com.project.teman_belajar.common.global_exception.dto.ErrorResponse;
import com.project.teman_belajar.common.global_exception.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalCustomException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions() {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected internal error occurred.",
                LocalDate.now().toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDate.now().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                LocalDate.now().toString()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        String errorMessage = (ex instanceof BadCredentialsException)
                ? "Email atau password salah."
                : "Anda tidak memiliki kredential atau token tidak valid.";

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                errorMessage,
                LocalDate.now().toString()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDate.now().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDate.now().toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound() {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Endpoint tidak ditemukan.",
                LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed() {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method HTTP tidak didukung.",
                LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse>  handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                LocalDate.now().toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
