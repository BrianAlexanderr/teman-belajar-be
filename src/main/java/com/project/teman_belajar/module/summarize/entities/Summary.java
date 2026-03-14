package com.project.teman_belajar.module.summarize.entities;

import com.project.teman_belajar.module.extract.entities.ExtractedText;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Summary {

    @Id
    @GeneratedValue
    private Integer id;

    private String text;

    private String name;

    private Date createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "extracted_text_id")
    private ExtractedText extractedText;

}
