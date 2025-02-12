package ru.klokov.backend.dto.emittertype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EmitterTypeRequest {
    @NotBlank(message = "Заполните поле \"тип излучателя\"!")
    @Size(min = 3, max = 50, message = "Тип излучателя должен состоять минимум из 3, и максимум из 50 символов!")
    private String name;
}
