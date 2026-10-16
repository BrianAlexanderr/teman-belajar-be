package com.project.teman_belajar.common.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isInvalidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        return !EMAIL_PATTERN.matcher(email).matches();
    }
}
