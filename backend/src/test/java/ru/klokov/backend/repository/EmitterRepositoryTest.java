package ru.klokov.backend.repository;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ru.klokov.backend.model.Emitter;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.model.EmitterType;

@DataJpaTest
class EmitterRepositoryTest {

    @Autowired
    private EmitterTypeRepository emitterTypeRepository;

    @Autowired
    private EmitterOwnerRepository emitterOwnerRepository;

    @Autowired
    private EmitterRepository emitterRepository;

    private EmitterType emitterType = EmitterType.builder().name("Type 1").build();

    private EmitterOwner emitterOwner = EmitterOwner.builder().name("Owner 1").build();

    private Emitter emitter = Emitter.builder()
            .emitterType(emitterType)
            .emitterOwner(emitterOwner)
            .factoryNumber("E1")
            .manufactureDate(LocalDate.now())
            .verificationPeriodicityInMonths(12)
            .forExternalUse(false)
            .hasInternalGenerator(true)
            .minimumPulseWidth(20.0)
            .maximumPulseWidth(100.0)
            .minimumPulseFrequency10(8.0)
            .maximumPulseFrequency10(12.0)
            .minimumPulseFrequency100(80.0)
            .maximumPulseFrequency100(120.0)
            .minimumPulseFrequency1000(800.0)
            .maximumPulseFrequency1000(1200.0)
            .minimumPulsePower(100.0)
            .maximumPulsePower(1000.0)
            .minimumRadiationFluxDivergenceAngle(8.0)
            .maximumRadiationFluxDivergenceAngle(12.0)
            .minimumNonParallelismOfTheOpticalAndConstructionAxis(8.0)
            .maximumNonParallelismOfTheOpticalAndConstructionAxis(12.0)
            .minimumUnevennessOfRadiationFLux(30.0)
            .maximumUnevennessOfRadiationFLux(70.0)
            .build();

    @BeforeEach
    void setUp() {
        emitterTypeRepository.deleteAll();
        emitterOwnerRepository.deleteAll();
        emitterRepository.deleteAll();
    }

    @Test
    @DisplayName("Test get all emitters functionality")
    void givenThreeStoredEmitters_whenFindAll_thenListOfThreeEmittersReturned() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        List<Emitter> savedEmittersList = emitterRepository.saveAll(List.of(emitter));

        // when
        List<Emitter> obtainedEmitters = emitterRepository.findAll();

        // then
        assertNotNull(obtainedEmitters);
        assertEquals(savedEmittersList.size(), obtainedEmitters.size());
    }

    @Test
    @DisplayName("Test get emitter by id functionality")
    void givenSavedEmitter_whenFindById_thenEmitterIsReturned() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        Emitter savedEmitter = emitterRepository.save(emitter);

        // when
        Emitter obtainedEmitter = emitterRepository.findById(emitter.getId()).orElse(null);

        // then
        assertNotNull(obtainedEmitter);
        assertEquals(savedEmitter.getFactoryNumber(), obtainedEmitter.getFactoryNumber());
    }

    @Test
    @DisplayName("Test emitter not found functionality")
    void givenEmitterIsNotCreated_whenFindById_thenEmptyOptionalIsRetunred() {
        // given
        emitter.setId(1L);

        // when
        Emitter obtainedEmitter = emitterRepository.findById(emitter.getId()).orElse(null);

        // then
        assertNull(obtainedEmitter);
    }

    @Test
    @DisplayName("Test save emitter functionality")
    void givenEmitterObjectToSave_whenSave_thenEmitterIsCreated() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        emitterRepository.save(emitter);

        // when
        Emitter createdEmitter = emitterRepository.findById(emitter.getId()).orElse(null);

        // then
        assertNotNull(createdEmitter);
        assertEquals(emitter.getId(), createdEmitter.getId());
    }

    @Test
    @DisplayName("Test update emitter functionality")
    void givenEmitterObjectToUpdate_whenSave_thenEmitterIsUpdated() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        String updatedFactoryNumber = "Updated factory number";
        Emitter savedEmitter = emitterRepository.save(emitter);

        // when
        Emitter obtainedEmitter = emitterRepository.findById(savedEmitter.getId()).orElse(null);
        obtainedEmitter.setFactoryNumber(updatedFactoryNumber);

        Emitter updatedEmitter = emitterRepository.save(obtainedEmitter);

        // then
        assertNotNull(updatedEmitter);
        assertEquals(updatedFactoryNumber, updatedEmitter.getFactoryNumber());
    }

    @Test
    @DisplayName("Test delete emitter by id functionality")
    void givenSavedEmitter_whenDeleteById_thenEmitterIsRemovedFromDatabase() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);
        emitterRepository.save(emitter);
        
        // when
        emitterRepository.deleteById(emitter.getId());

        // then
        Emitter obtainedEmitter = emitterRepository.findById(emitter.getId()).orElse(null);
        assertNull(obtainedEmitter);
    }
}
