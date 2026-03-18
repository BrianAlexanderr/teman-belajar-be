package com.project.teman_belajar.module.auth.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 2048)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "users_id")
    private Users user;

}
