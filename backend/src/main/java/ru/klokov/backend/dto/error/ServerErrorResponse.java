package ru.klokov.backend.dto.error;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Ответ с информацией об ошибке сервера")
@AllArgsConstructor
@Getter
public class ServerErrorResponse {
    @Schema(description = "Статус код ошибки", example = "404")
    private final int statusCode;
    @Schema(description = "Сообщение с описанием ошибки", example = "Объект не найден по заданному идентификатору")
    private final String message;
    @Schema(description = "Время ошибки")
    private final Instant timestamp;
}
