package com.test.izertis.dtos.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtTokenDTO {
    private final String token;
}
