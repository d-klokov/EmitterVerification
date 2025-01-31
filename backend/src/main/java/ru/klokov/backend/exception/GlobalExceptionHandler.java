package ru.klokov.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.klokov.backend.dto.ApiErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ ApiException.class })
    protected ResponseEntity<ApiErrorResponse> handleApiException(ApiException exception) {
        return new ResponseEntity<>(
                new ApiErrorResponse(
                        exception.getStatus().value(),
                        exception.getMessage(),
                        exception.getTimestamp()),
                exception.getStatus());
    }

}
