package com.test.izertis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String givenName;

    private String familyName;

    private String nationality;

    @Column(unique = true)
    private String email;

    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false, referencedColumnName = "id")
    private Club club;
}
