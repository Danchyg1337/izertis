package com.test.izertis.dtos.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewClubDTO {
    @NotBlank
    @Email
    private String username;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    private String officialName;

    @NotBlank
    private String popularName;

    @NotBlank
    @Size(max = 8)
    private String federation;

    @NotNull
    private Boolean isPublic;
}
