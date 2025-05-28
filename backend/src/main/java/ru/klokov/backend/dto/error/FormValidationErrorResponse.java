package ru.klokov.backend.dto.error;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FormValidationErrorResponse {
    private final int statusCode;
    private final Map<String, String> errors;
    private final Instant timestamp;
}
