package ru.klokov.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.klokov.backend.model.EmitterOwner;

@Repository
public interface EmitterOwnerRepository extends JpaRepository<EmitterOwner, Long> {

}
