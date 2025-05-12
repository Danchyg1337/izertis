package com.test.izertis.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ClubResponseDTO {
    private Long id;

    private String officialName;

    private String popularName;

    private String federation;

    private Boolean isPublic;

    private Long numberOfPlayers;
}
