package ru.klokov.backend.dto.error;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Ответ с информацией об ошибке валидации")
@AllArgsConstructor
@Getter
public class FormValidationErrorResponse {
    @Schema(description = "Статус код ошибки", example = "400")
    private final int statusCode;
    @Schema(description = "Список ошибок в формате \"поле\":\"сообщение\"", example = "{\"name\":[\"Заполните поле \"Тип излучателя\"\"]}")
    private final Map<String, List<String>> errors;
    @Schema(description = "Время ошибки")
    private final Instant timestamp;
}
