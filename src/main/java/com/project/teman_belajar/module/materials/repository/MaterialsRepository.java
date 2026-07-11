package com.project.teman_belajar.module.materials.repository;

import com.project.teman_belajar.module.materials.entities.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MaterialsRepository extends JpaRepository<Materials, UUID> {

    List<Materials> findByFolders_IdAndStatus(UUID folderId, String status);

    @Modifying
    @Query("DELETE FROM Materials m WHERE m.status = :status AND m.createdAt < :expirationTime")
    int deleteByStatusAndCreatedAtBefore(
            @Param("status") String status,
            @Param("expirationTime") LocalDateTime expirationTime
    );

}
