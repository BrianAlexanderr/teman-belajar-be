package com.project.teman_belajar.module.quiz.repository;

import com.project.teman_belajar.module.quiz.entities.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionsRepository extends JpaRepository<Questions, UUID> {
    List<Questions> findByQuizId(UUID quizId);
}
