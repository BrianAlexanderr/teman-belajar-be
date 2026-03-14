package com.project.teman_belajar.module.folder.repository;

import com.project.teman_belajar.module.folder.entities.Folders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoldersRepository extends JpaRepository<Folders, Integer> {
    Optional<List<Folders>> findByUserId(Integer id);
    Optional<Folders> findByNameAndUserId(String name, Integer id);
}
