package ru.klokov.backend.dto.error;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParameterValidationErrorResponse {
    private final int statusCode;
    private final String message;
    private final Instant timestamp;
}
