package com.project.teman_belajar.module.users.entities;

import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.quiz.entities.QuizAttempts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Users {
    @Id
    private Integer id;

    private String displayId;

    private String name;

    private String email;

    private boolean isSubscribe;

    private String passwordHashed;

    private Date createdAt;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Materials> materialList;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttempts> quizAttemptsList;
}
