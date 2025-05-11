package com.test.izertis.exception;

import org.springframework.http.HttpStatusCode;

public class InsufficientAuthoritiesException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public InsufficientAuthoritiesException(HttpStatusCode status, String message) {
        super(message);
        this.statusCode = status;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
