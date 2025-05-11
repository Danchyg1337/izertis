package com.test.izertis.dtos.response;

import lombok.Data;

@Data
public class ClubDetailsDTO {
    private Long id;

    private String officialName;

    private String popularName;

    private String federation;

    private Boolean isPublic;

    private Long numberOfPlayers;
}
