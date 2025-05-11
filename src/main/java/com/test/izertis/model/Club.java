package com.test.izertis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(length = 100)
    private String password;

    private String officialName;

    private String popularName;

    @Column(length = 8)
    private String federation;

    private Boolean isPublic;

    @OneToMany(mappedBy = "club")
    private List<Player> players;
}
