package com.project.teman_belajar.module.auth.repository;

import com.project.teman_belajar.module.auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(UUID id);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.id = :tokenId")
    void deleteByIds(@Param("tokenId") UUID tokenId);
}
