package com.project.teman_belajar.module.quiz.entities;

import com.project.teman_belajar.module.auth.entities.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class QuizAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer score;

    private Date attemptAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
