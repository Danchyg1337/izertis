package com.test.izertis.dtos.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PlayerDetailsDTO {
    private long id;

    private String givenName;

    private String familyName;

    private String nationality;

    private String email;

    private LocalDate dateOfBirth;
}
