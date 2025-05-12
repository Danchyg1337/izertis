package com.test.izertis.exception;

import org.springframework.http.HttpStatus;

public class InsufficientAuthoritiesException extends RuntimeException {

    private final HttpStatus status;

    public InsufficientAuthoritiesException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatusCode() {
        return status;
    }
}
