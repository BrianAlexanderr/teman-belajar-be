package com.project.teman_belajar.module.summarize.entities;

import com.project.teman_belajar.common.entity.BaseEntity;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.quiz.entities.Quiz;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Summary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String path;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folders_id")
    private Folders folders;

    @OneToMany(mappedBy = "summary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizList;

}
