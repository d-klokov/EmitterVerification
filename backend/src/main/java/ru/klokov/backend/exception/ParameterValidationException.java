package ru.klokov.backend.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ParameterValidationException extends RuntimeException {
    private final HttpStatus status;
    private final Instant timestamp;

    public ParameterValidationException(HttpStatus status, String message, Instant timestamp) {
        super(message);
        this.status = status;
        this.timestamp = timestamp;
    }

    public ParameterValidationException(HttpStatus status) {
        this.status = status;
        this.timestamp = Instant.now();
    }

    public ParameterValidationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.timestamp = Instant.now();
    }
}
