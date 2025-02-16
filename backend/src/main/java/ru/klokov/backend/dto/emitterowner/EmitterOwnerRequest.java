package ru.klokov.backend.dto.emitterowner;

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
public class EmitterOwnerRequest {
    @NotBlank(message = "Заполните поле \"владелец\"!")
    @Size(min = 3, max = 50, message = "Имя владельца должно состоять минимум из 3, и максимум из 50 символов!")
    private String name;
}
