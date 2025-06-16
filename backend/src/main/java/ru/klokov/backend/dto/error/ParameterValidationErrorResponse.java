package ru.klokov.backend.dto.error;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Ответ с информацией об ошибке недопустимого параметра")
@AllArgsConstructor
@Getter
public class ParameterValidationErrorResponse {
    @Schema(description = "Статус код ошибки", example = "400")
    private final int statusCode;
    @Schema(description = "Сообщение с описанием ошибки", example = "Некорректный параметр \"Номер страницы\"")
    private final String message;
    @Schema(description = "Время ошибки")
    private final Instant timestamp;
}
