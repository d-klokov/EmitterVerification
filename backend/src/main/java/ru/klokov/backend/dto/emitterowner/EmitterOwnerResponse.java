package ru.klokov.backend.dto.emitterowner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitterOwnerResponse {
    private Long id;
    private String name;
}
