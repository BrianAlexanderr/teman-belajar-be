package com.project.teman_belajar.module.materials.repository;

import com.project.teman_belajar.module.materials.entities.DeletedMaterials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeletedMaterialRepository extends JpaRepository<DeletedMaterials, UUID> {
    @Query(value = "SELECT CAST(id AS VARCHAR) FROM deleted_materials LIMIT 500", nativeQuery = true)
    List<String> findIdsWithLimit();

    @Modifying
    @Query(value = "INSERT INTO deleted_materials (id) VALUES (:id)", nativeQuery = true)
    void insertDeletedMaterial(@Param("id") UUID id);
}
