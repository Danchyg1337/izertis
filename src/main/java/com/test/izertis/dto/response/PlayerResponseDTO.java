package com.test.izertis.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(Include.NON_NULL)
public class PlayerResponseDTO {
    private long id;

    private String givenName;

    private String familyName;

    private String nationality;

    private String email;

    private LocalDate dateOfBirth;
}
