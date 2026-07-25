package com.project.teman_belajar.module.summarize.repository;

import com.project.teman_belajar.module.summarize.entities.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, UUID> {

    List<Summary> findSummaryByFoldersId(UUID folderId);
}
