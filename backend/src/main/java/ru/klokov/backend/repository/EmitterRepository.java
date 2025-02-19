package ru.klokov.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.klokov.backend.model.Emitter;

@Repository
public interface EmitterRepository extends JpaRepository<Emitter, Long> {
    Emitter findByFactoryNumber(String factoryNumber);
}
