package com.project.teman_belajar.module.materials.entities;

import com.project.teman_belajar.module.extract.entities.ExtractedText;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.users.entities.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Materials {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String url;

    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folders_id")
    private Folders folders;

    @OneToOne(mappedBy = "materials")
    private ExtractedText extractedText;
}
