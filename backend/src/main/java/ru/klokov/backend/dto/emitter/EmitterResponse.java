package ru.klokov.backend.dto.emitter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.klokov.backend.dto.emitterowner.EmitterOwnerResponse;
import ru.klokov.backend.dto.emittertype.EmitterTypeResponse;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitterResponse {
    private Long id;
    private String factoryNumber;
    private LocalDate manufactureDate;
    private Integer verificationPeriodicityInMonths;
    private Boolean forExternalUse;
    private Boolean hasInternalGenerator;

    private Double minimumPulseWidth;
    private Double maximumPulseWidth;

    private Double minimumPulseFrequency10;
    private Double maximumPulseFrequency10;

    private Double minimumPulseFrequency100;
    private Double maximumPulseFrequency100;

    private Double minimumPulseFrequency1000;
    private Double maximumPulseFrequency1000;

    private Integer minimumPulsePower;
    private Integer maximumPulsePower;

    private Double minimumRadiationFluxDivergenceAngle;
    private Double maximumRadiationFluxDivergenceAngle;

    private Double minimumNonParallelismOfTheOpticalAndConstructionAxis;
    private Double maximumNonParallelismOfTheOpticalAndConstructionAxis;

    private Double minimumUnevennessOfRadiationFLux;
    private Double maximumUnevennessOfRadiationFLux;

    private EmitterTypeResponse type;
    private EmitterOwnerResponse owner;
}
