package ru.klokov.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import ru.klokov.backend.dto.error.FormValidationErrorResponse;
import ru.klokov.backend.dto.error.ParameterValidationErrorResponse;
import ru.klokov.backend.dto.error.ServerErrorResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
        @ExceptionHandler({ ParameterValidationException.class })
        protected ResponseEntity<ParameterValidationErrorResponse> handleParameterValidationException(
                        ParameterValidationException exception) {
                
                log.error("Parameter validation error: {}", exception.getMessage(), exception);

                return new ResponseEntity<>(
                                new ParameterValidationErrorResponse(
                                        exception.getStatus().value(),
                                        exception.getMessage(), 
                                        exception.getTimestamp()),
                                exception.getStatus());
        }

        @ExceptionHandler({ FormValidationException.class })
        protected ResponseEntity<FormValidationErrorResponse> handleFormValidationException(FormValidationException exception) {

                log.error("Form validation error: {}", exception.getMessage(), exception);

                return new ResponseEntity<>(
                                new FormValidationErrorResponse(
                                                exception.getStatus().value(),
                                                exception.getErrors(),
                                                exception.getTimestamp()),
                                exception.getStatus());
        }

        @ExceptionHandler({ ServerException.class })
        protected ResponseEntity<ServerErrorResponse> handleServerException(ServerException exception) {

                log.error("Server error: {}", exception.getMessage(), exception);

                return new ResponseEntity<>(
                                new ServerErrorResponse(
                                                exception.getStatus().value(),
                                                exception.getMessage(),
                                                exception.getTimestamp()),
                                exception.getStatus());
        }
}
