package com.project.teman_belajar.module.quiz.repository;

import com.project.teman_belajar.module.quiz.entities.Quiz;
import com.project.teman_belajar.module.quiz.projection.PreviewQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    List<PreviewQuiz> findAllByUserId(UUID userId);

}
