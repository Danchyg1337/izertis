package com.test.izertis.dtos.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewPlayerDTO {
    @NotNull
    private String givenName;

    @NotNull
    private String familyName;

    @NotNull
    private String nationality;

    @NotNull
    @Email
    private String email;

    @Past
    private LocalDate dateOfBirth;
}
