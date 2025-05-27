CREATE TABLE IF NOT EXISTS emitter_type (
    id BIGSERIAL PRIMARY KEY,
    type_name VARCHAR(255) not null unique
);