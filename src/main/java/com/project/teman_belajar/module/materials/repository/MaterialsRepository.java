package com.project.teman_belajar.module.materials.repository;

import com.project.teman_belajar.module.materials.entities.Materials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MaterialsRepository extends JpaRepository<Materials, UUID> {

    List<Materials> findByFolders_IdAndStatus(UUID folderId, String status);

    List<Materials> findByStatusAndCreatedAtBefore(
            String status,
            LocalDateTime expireTime
    );

}
