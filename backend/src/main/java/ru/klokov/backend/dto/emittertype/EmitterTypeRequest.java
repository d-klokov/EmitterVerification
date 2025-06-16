package ru.klokov.backend.dto.emittertype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "Запрос на создание или обновление типа излучателя", example = "{\"name\": \"Тип №1\"}")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EmitterTypeRequest {
    @Schema(description = "Название  типа излучателя", example = "Тип №1", required = true)
    @NotBlank(message = "Заполните поле \"Тип излучателя\"")
    @Size(min = 3, max = 255, message = "Тип излучателя должен состоять минимум из 3 символов")
    private String name;
}
