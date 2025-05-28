package ru.klokov.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.klokov.backend.model.EmitterType;

@Repository
public interface EmitterTypeRepository extends JpaRepository<EmitterType, Long> {

}
