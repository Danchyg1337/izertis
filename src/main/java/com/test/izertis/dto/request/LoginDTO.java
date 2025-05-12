package com.test.izertis.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank
    @Email
    private String username;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
