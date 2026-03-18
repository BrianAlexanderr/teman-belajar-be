package com.project.teman_belajar.module.auth.service;

import com.project.teman_belajar.module.auth.dto.request.RefreshTokenRequest;
import com.project.teman_belajar.module.auth.dto.response.AuthenticationResponse;
import com.project.teman_belajar.module.auth.dto.response.DeleteRefreshTokenResponse;
import com.project.teman_belajar.module.auth.entities.RefreshToken;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.auth.exception.custom_exception.TokenExpiredException;
import com.project.teman_belajar.module.auth.exception.custom_exception.TokenRefreshException;
import com.project.teman_belajar.module.auth.exception.custom_exception.UserNotFoundException;
import com.project.teman_belajar.module.auth.repository.RefreshTokenRepository;
import com.project.teman_belajar.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public RefreshToken createRefreshToken(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String jwtRefreshToken = jwtService.generateRefreshToken(user);

        long refreshTokenDurationMs = 604800000;
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(jwtRefreshToken)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    private void deleteRefreshToken(UUID id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found");
        }

        refreshTokenRepository.deleteByUserId(id);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken, UUID userId) {
        if(refreshToken.getExpiryDate().compareTo(Instant.now()) < 0){
            deleteRefreshToken(userId);
            throw new TokenExpiredException("Refresh Token Expired");
        }
        return refreshToken;
    }


    public ResponseEntity<DeleteRefreshTokenResponse> deleteByUserId(UUID userId) {

        deleteRefreshToken(userId);

        DeleteRefreshTokenResponse response = new DeleteRefreshTokenResponse(
                "Success",
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public AuthenticationResponse generateNewToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.token();

        return findByToken(requestRefreshToken)
                .map(token -> verifyExpiration(token, token.getId()))
                .map(RefreshToken::getUser)
                .map(user -> {
                    if (jwtService.validateToken(requestRefreshToken, user)) {

                        String newAccessToken = jwtService.generateAccessToken(user);

                        return new AuthenticationResponse(newAccessToken, requestRefreshToken);
                    }
                    throw new TokenRefreshException(requestRefreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    public RefreshToken getOrCreateRefreshToken(UUID id) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(id);

        if (existingToken.isPresent()) {
            RefreshToken refreshToken = existingToken.get();

            if (refreshToken.getExpiryDate().compareTo(Instant.now()) > 0) {
                return refreshToken;
            } else {
                deleteRefreshToken(id);
            }
        }

        return createRefreshToken(id);
    }
}
