package com.project.teman_belajar.module.materials.entities;

import com.project.teman_belajar.module.extract.entities.ExtractedText;
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
    private Integer id;

    private String displayId;

    private String name;

    private String url;

    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToOne(mappedBy = "extracted_text")
    private ExtractedText extractedText;
}
