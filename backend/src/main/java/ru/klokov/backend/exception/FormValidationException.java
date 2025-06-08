package ru.klokov.backend.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class FormValidationException extends RuntimeException {

    private final Map<String, List<String>> errors;
    private final HttpStatus status;
    private final Instant timestamp;

    public FormValidationException(HttpStatus status, Map<String, List<String>> errors, Instant timestamp) {
        this.errors = errors;
        this.status = status;
        this.timestamp = timestamp;
    }

    public FormValidationException(HttpStatus status) {
        this.errors = new HashMap<>();
        this.status = status;
        this.timestamp = Instant.now();
    }
}
