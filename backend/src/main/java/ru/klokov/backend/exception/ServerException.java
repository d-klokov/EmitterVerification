package ru.klokov.backend.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private final HttpStatus status;
    private final Instant timestamp;

    public ServerException(HttpStatus status, String message, Instant timestamp) {
        super(message);
        this.status = status;
        this.timestamp = timestamp;
    }

    public ServerException(HttpStatus status) {
        this.status = status;
        this.timestamp = Instant.now();
    }

    public ServerException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.timestamp = Instant.now();
    }
}
