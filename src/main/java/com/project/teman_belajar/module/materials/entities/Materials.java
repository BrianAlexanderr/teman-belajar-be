package com.project.teman_belajar.module.materials.entities;

import com.project.teman_belajar.module.extract.entities.ExtractedText;
import com.project.teman_belajar.module.folder.entities.Folders;
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
public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String url;

    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folders_id")
    private Folders folders;

    @OneToOne(mappedBy = "materials")
    private ExtractedText extractedText;
}
