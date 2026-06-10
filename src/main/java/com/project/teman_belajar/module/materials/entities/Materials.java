package com.project.teman_belajar.module.materials.entities;

import com.project.teman_belajar.common.entity.BaseEntity;
import com.project.teman_belajar.module.folder.entities.Folders;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Materials extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String path;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folders_id")
    private Folders folders;
}
