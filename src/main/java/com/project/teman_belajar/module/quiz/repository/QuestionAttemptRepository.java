package com.project.teman_belajar.module.quiz.repository;

import com.project.teman_belajar.module.quiz.entities.QuestionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, UUID> {
}
