package com.project.teman_belajar.module.folder.entities;

import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.auth.entities.Users;
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
public class Folders {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "folders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Materials> materialsList;

}
