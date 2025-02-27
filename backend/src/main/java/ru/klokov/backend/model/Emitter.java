package ru.klokov.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "emitter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Emitter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "factory_number", nullable = false)
    private String factoryNumber;

    @Column(name = "manufacture_date", nullable = false)
    private LocalDate manufactureDate;

    @Column(name = "verification_periodicity_in_months", nullable = false)
    private Integer verificationPeriodicityInMonths;

    @Column(name = "for_external_use", nullable = false)
    private Boolean forExternalUse;

    @Column(name = "has_internal_generator", nullable = false)
    private Boolean hasInternalGenerator;

    @Column(name = "minimum_pulse_width", nullable = false)
    private Double minimumPulseWidth;
    @Column(name = "maximum_pulse_width", nullable = false)
    private Double maximumPulseWidth;

    @Column(name = "minimum_pulse_frequency_10")
    private Double minimumPulseFrequency10;
    @Column(name = "maximum_pulse_frequency_10")
    private Double maximumPulseFrequency10;

    @Column(name = "minimum_pulse_frequency_100")
    private Double minimumPulseFrequency100;
    @Column(name = "maximum_pulse_frequency_100")
    private Double maximumPulseFrequency100;

    @Column(name = "minimum_pulse_frequency_1000", nullable = false)
    private Double minimumPulseFrequency1000;
    @Column(name = "maximum_pulse_frequency_1000", nullable = false)
    private Double maximumPulseFrequency1000;

    @Column(name = "minimum_pulse_power", nullable = false)
    private Double minimumPulsePower;
    @Column(name = "maximum_pulse_power", nullable = false)
    private Double maximumPulsePower;

    @Column(name = "minimum_radiation_flux_divergence_angle", nullable = false)
    private Double minimumRadiationFluxDivergenceAngle;
    @Column(name = "maximum_radiation_flux_divergence_angle", nullable = false)
    private Double maximumRadiationFluxDivergenceAngle;

    @Column(name = "minimum_non_parallelism_of_the_optical_and_construction_axis", nullable = false)
    private Double minimumNonParallelismOfTheOpticalAndConstructionAxis;
    @Column(name = "maximum_non_parallelism_of_the_optical_and_construction_axis", nullable = false)
    private Double maximumNonParallelismOfTheOpticalAndConstructionAxis;

    @Column(name = "minimum_unevenness_of_radiation_flux", nullable = false)
    private Double minimumUnevennessOfRadiationFLux;
    @Column(name = "maximum_unevenness_of_radiation_flux", nullable = false)
    private Double maximumUnevennessOfRadiationFLux;

    @ManyToOne
    @JoinColumn(name = "emitter_type_id")
    private EmitterType emitterType;

    @ManyToOne
    @JoinColumn(name = "emitter_owner_id")
    private EmitterOwner emitterOwner;

    // TODO: verifications
    // @OneToMany(mappedBy = "emitter")
    // private List<Verification> verifications = new ArrayList<>();
}

