package com.test.izertis.controller.handler;

import com.test.izertis.dto.response.error.ErrorResponseDTO;
import com.test.izertis.exception.ConflictException;
import com.test.izertis.exception.InsufficientAuthoritiesException;
import com.test.izertis.exception.InvalidCredentialsException;
import com.test.izertis.exception.ResourceNotFoundException;
import com.test.izertis.service.validator.Messages;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Messages messages;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDTO.setError(HttpStatus.BAD_REQUEST.name());
        errorResponseDTO.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDTO.setError(HttpStatus.BAD_REQUEST.name());

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        errorResponseDTO.setMessage(fieldErrors);

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientAuthoritiesException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientAuthoritiesError(InsufficientAuthoritiesException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(ex.getStatusCode().value());
        errorResponseDTO.setError(ex.getStatusCode().name());
        errorResponseDTO.setMessage(decodeError(ex.getMessage()));

        return new ResponseEntity<>(errorResponseDTO, ex.getStatusCode());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundError(ResourceNotFoundException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponseDTO.setError(HttpStatus.NOT_FOUND.name());
        errorResponseDTO.setMessage(decodeError(ex.getMessage()).getLeft());

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictError(ConflictException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(HttpStatus.CONFLICT.value());
        errorResponseDTO.setError(HttpStatus.CONFLICT.name());
        errorResponseDTO.setMessage(decodeError(ex.getMessage()).getLeft());

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponseDTO.setError(HttpStatus.UNAUTHORIZED.name());
        errorResponseDTO.setMessage(decodeError(ex.getMessage()).getLeft());

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    private Pair<String, String> decodeError(String message) {
        if (StringUtils.startsWith(message, "{") && StringUtils.endsWith(message, "}")) {
            String errorCode;
            String errorMessage;
            if (message.contains("#")) {
                errorCode = StringUtils.substringBetween(message, "{", "#");
                String[] params = StringUtils.split(StringUtils.substringBetween(message, "#", "}"), '|');
                String formatMessage = messages.getOptional(errorCode).orElse(message);
                if (params.length > 0) {
                    errorMessage = String.format(formatMessage, (Object[]) params);
                } else {
                    errorMessage = formatMessage;
                }
            } else {
                errorCode = StringUtils.substringBetween(message, "{", "}");
                errorMessage = messages.getOptional(errorCode)
                        .orElse(message);
            }
            return Pair.of(errorMessage, errorCode);
        }
        return Pair.of(message, null);
    }
}
