package com.test.izertis.entity;

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
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "official_name")
    private String officialName;

    @Column(name = "popular_name")
    private String popularName;

    @Column(name = "federation", length = 8)
    private String federation;

    @Column(name = "is_public")
    private Boolean isPublic;

    @OneToMany(mappedBy = "club")
    private List<Player> players;
}
