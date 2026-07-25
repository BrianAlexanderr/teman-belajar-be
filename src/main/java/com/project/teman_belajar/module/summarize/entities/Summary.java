package com.project.teman_belajar.module.summarize.entities;

import com.project.teman_belajar.common.entity.BaseEntity;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.quiz.entities.Quiz;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    private String title;

    private String preview;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "key_points", columnDefinition = "jsonb")
    private List<String> keyPoints;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folders_id")
    private Folders folders;

    @OneToMany(mappedBy = "summary", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Quiz> quizList;

}
