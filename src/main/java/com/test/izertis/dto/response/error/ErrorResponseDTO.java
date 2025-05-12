package com.test.izertis.dto.response.error;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private int status;
    private String error;
    private Object message;
}
