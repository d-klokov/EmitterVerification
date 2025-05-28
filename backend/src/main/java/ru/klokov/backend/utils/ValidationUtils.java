package ru.klokov.backend.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;

public class ValidationUtils {
    public static Map<String, String> getErrorMessages(List<FieldError> errors) {
        Map<String, String> errorMessages = new HashMap<>();
        errors.forEach(error -> errorMessages.put(error.getField(), error.getDefaultMessage()));

        return errorMessages;
    }
}
