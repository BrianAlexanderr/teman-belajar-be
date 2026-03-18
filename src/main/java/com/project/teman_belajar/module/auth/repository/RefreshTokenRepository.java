package com.project.teman_belajar.module.auth.repository;

import com.project.teman_belajar.module.auth.entities.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(UUID id);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken  r where r.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID id);
}
