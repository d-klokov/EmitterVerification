package ru.klokov.backend.dto.emittertype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Ответ c информацией о типе излучателя")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitterTypeResponse {
    @Schema(description = "Уникальный идентификатор типа излучателя", example = "1")
    private Long id;
    @Schema(description = "Название типа излучателя", example = "Тип №1")
    private String name;
}
