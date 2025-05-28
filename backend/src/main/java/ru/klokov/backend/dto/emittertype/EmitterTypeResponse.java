package ru.klokov.backend.dto.emittertype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitterTypeResponse {
    private Long id;
    private String name;
}
