package ru.klokov.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import ru.klokov.backend.dto.AppErrorResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({ AppException.class })
    protected ResponseEntity<AppErrorResponse> handleApiException(AppException exception) {
        log.error("Error!", exception);
        
        return new ResponseEntity<>(
                new AppErrorResponse(
                        exception.getStatus().value(),
                        exception.getMessage(),
                        exception.getTimestamp()),
                exception.getStatus());
    }
}
