package com.project.teman_belajar.module.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;

    @Value("${OTP_PREFIX}")
    private String OTP_PREFIX;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public String generateAndStoreOtp(String email) {
        SecureRandom random = new SecureRandom();

        String otp = IntStream.range(0, 6)
                .map(i -> ALPHA_NUMERIC_STRING.charAt(random.nextInt(ALPHA_NUMERIC_STRING.length())))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());

        String key = OTP_PREFIX + email;

        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(OTP_EXPIRATION_MINUTES));

        return otp;
    }

    public boolean validOtp(String email, String userEnteredOtp) {
        if (userEnteredOtp == null) return false;

        String key = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);


        if (storedOtp != null && storedOtp.equalsIgnoreCase(userEnteredOtp)) {
            return true;
        }
        return false;
    }

    public void deleteOtp(String email) {
        String key = OTP_PREFIX + email;

        redisTemplate.delete(key);
    }
}
