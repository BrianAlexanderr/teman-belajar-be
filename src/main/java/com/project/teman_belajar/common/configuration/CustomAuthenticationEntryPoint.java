package com.project.teman_belajar.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.teman_belajar.common.global_exception.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String errorMessage = getString(authException);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                errorMessage,
                LocalDateTime.now().toString()
        );

        OutputStream out = response.getOutputStream();
        objectMapper.writeValue(out, errorResponse);
        out.flush();
    }

    private static @NonNull String getString(@NonNull AuthenticationException authException) {
        String errorMessage;

        if (authException instanceof BadCredentialsException) {
            errorMessage = "Email atau password salah.";
        } else {
            errorMessage = "Anda tidak memiliki kredential atau token tidak valid.";
        }

        return errorMessage;
    }
}
