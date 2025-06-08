package ru.klokov.backend.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;

public class ValidationUtils {
    public static Map<String, List<String>> getErrorMessages(List<FieldError> errors) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        
        for (FieldError error : errors) {
            errorMessages.computeIfAbsent(error.getField(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }

        return errorMessages;

        // Map<String, String> errorMessages = new HashMap<>();
        // errors.forEach(error -> errorMessages.put(error.getField(), error.getDefaultMessage()));

        // return errorMessages;
    }
}
