package ru.klokov.backend.dto.error;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FormValidationErrorResponse {
    private final int statusCode;
    private final Map<String, List<String>> errors;
    private final Instant timestamp;
}
