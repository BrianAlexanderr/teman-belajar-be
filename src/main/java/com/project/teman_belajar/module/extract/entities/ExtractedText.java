package com.project.teman_belajar.module.extract.entities;

import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.quiz.entities.Quiz;
import com.project.teman_belajar.module.summarize.entities.Summary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ExtractedText {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String text;

    private Date createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "materials_id")
    private Materials materials;

    @OneToOne(mappedBy = "extractedText", cascade = CascadeType.ALL, orphanRemoval = true)
    private Quiz quiz;

    @OneToOne(mappedBy = "extractedText", cascade = CascadeType.ALL, orphanRemoval = true)
    private Summary summary;

}
