package com.project.teman_belajar.module.folder.entities;

import com.project.teman_belajar.common.entity.BaseEntity;
import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.summarize.entities.Summary;
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
public class Folders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "folders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Materials> materialsList;

    @OneToMany(mappedBy = "folders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Summary> summaryList;

}
