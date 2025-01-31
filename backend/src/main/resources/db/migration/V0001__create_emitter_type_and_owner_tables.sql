create table if not exists emitter_type (
    id bigserial primary key,
    type_name varchar(50) not null unique
);

create table if not exists emitter_owner (
    id bigserial primary key,
    owner_name varchar(50) not null unique
);