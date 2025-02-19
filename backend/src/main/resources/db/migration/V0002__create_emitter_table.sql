create table if not exists emitter (
    id bigserial primary key,
    factory_number varchar(50) not null unique,
    manufacture_date date not null,
    verification_periodicity_in_months integer not null,
    for_external_use bool not null,
    has_internal_generator bool,

    minimum_pulse_width double precision not null,
    maximum_pulse_width double precision not null,

    minimum_pulse_frequency_10 double precision,
    maximum_pulse_frequency_10 double precision,
    minimum_pulse_frequency_100 double precision,
    maximum_pulse_frequency_100 double precision,
    minimum_pulse_frequency_1000 double precision not null,
    maximum_pulse_frequency_1000 double precision not null,

    minimum_pulse_power double precision not null,
    maximum_pulse_power double precision not null,

    minimum_radiation_flux_divergence_angle double precision not null,
    maximum_radiation_flux_divergence_angle double precision not null,

    minimum_non_parallelism_of_the_optical_and_construction_axis double precision not null,
    maximum_non_parallelism_of_the_optical_and_construction_axis double precision not null,

    minimum_unevenness_of_radiation_flux double precision not null,
    maximum_unevenness_of_radiation_flux double precision not null,

    emitter_type_id bigserial references emitter_type(id),
    emitter_owner_id bigserial references emitter_owner(id)
);