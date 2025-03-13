CREATE TABLE emitter (
    id BIGSERIAL PRIMARY KEY,

    factory_number VARCHAR(255) NOT NULL UNIQUE,
    manufacture_date DATE NOT NULL,
    verification_periodicity_in_months INTEGER NOT NULL,
    for_external_use bool NOT NULL,
    has_internal_generator BOOLEAN NOT NULL,

    minimum_pulse_width DOUBLE PRECISION NOT NULL,
    maximum_pulse_width DOUBLE PRECISION NOT NULL,

    minimum_pulse_frequency_10 DOUBLE PRECISION,
    maximum_pulse_frequency_10 DOUBLE PRECISION,
    minimum_pulse_frequency_100 DOUBLE PRECISION,
    maximum_pulse_frequency_100 DOUBLE PRECISION,
    minimum_pulse_frequency_1000 DOUBLE PRECISION,
    maximum_pulse_frequency_1000 DOUBLE PRECISION,

    minimum_pulse_power DOUBLE PRECISION NOT NULL,
    maximum_pulse_power DOUBLE PRECISION NOT NULL,

    minimum_radiation_flux_divergence_angle DOUBLE PRECISION NOT NULL,
    maximum_radiation_flux_divergence_angle DOUBLE PRECISION NOT NULL,

    minimum_non_parallelism_of_the_optical_and_construction_axis DOUBLE PRECISION NOT NULL,
    maximum_non_parallelism_of_the_optical_and_construction_axis DOUBLE PRECISION NOT NULL,

    minimum_unevenness_of_radiation_flux DOUBLE PRECISION NOT NULL,
    maximum_unevenness_of_radiation_flux DOUBLE PRECISION NOT NULL,

    emitter_type_id BIGSERIAL NOT NULL REFERENCES emitter_type(id),
    emitter_owner_id BIGSERIAL NOT NULL REFERENCES emitter_owner(id)

    CHECK(
        (has_internal_generator = FALSE AND
            minimum_pulse_frequency_10 IS NOT NULL AND maximum_pulse_frequency_10 IS NOT NULL AND
            minimum_pulse_frequency_100 IS NOT NULL AND maximum_pulse_frequency_100 IS NOT NULL AND
            minimum_pulse_frequency_1000 IS NOT NULL AND maximum_pulse_frequency_1000 IS NOT NULL
        )
        OR
        (has_internal_generator = TRUE AND
            minimum_pulse_frequency_10 IS NULL AND maximum_pulse_frequency_10 IS NULL AND
            minimum_pulse_frequency_100 IS NULL AND maximum_pulse_frequency_100 IS NULL AND
            minimum_pulse_frequency_1000 IS NULL AND maximum_pulse_frequency_1000 IS NULL
        )
    )
);