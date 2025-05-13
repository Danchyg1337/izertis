package com.test.izertis.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlayerRequestDTO {
    @NotBlank
    private String givenName;

    @NotBlank
    private String familyName;

    @NotBlank
    private String nationality;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Past
    private LocalDate dateOfBirth;
}
