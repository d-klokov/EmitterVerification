package ru.klokov.backend.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppErrorResponse {

    private final int statusCode;
    private final String message;
    private final Instant timestamp;

}
