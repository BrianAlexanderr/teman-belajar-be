package com.project.teman_belajar.module.extract.entities;

import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.quiz.entities.Quiz;
import com.project.teman_belajar.module.summarize.entities.Summary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ExtractedText {

    @Id
    private Integer id;

    private String displayId;

    private String name;

    private String text;

    private Date createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "materials_id")
    private Materials materials;

    @OneToOne(mappedBy = "extracted_text", cascade = CascadeType.ALL, orphanRemoval = true)
    private Quiz quiz;

    @OneToOne(mappedBy = "extracted_text", cascade = CascadeType.ALL, orphanRemoval = true)
    private Summary summary;

}
