package ru.klokov.backend.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final Instant timestamp;

    public ApiException(HttpStatus status, String message, Instant timestamp) {
        super(message);
        this.status = status;
        this.timestamp = timestamp;
    }

    public ApiException(HttpStatus status) {
        this.status = status;
        this.timestamp = Instant.now();
    }
}
