package com.project.teman_belajar.module.auth.service;

import com.project.teman_belajar.module.auth.entities.RefreshToken;
import com.project.teman_belajar.module.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenDeleteService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.deleteByIds(refreshToken.getId());
    }

}
