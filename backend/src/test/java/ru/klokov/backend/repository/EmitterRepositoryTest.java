package ru.klokov.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.Emitter;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.model.EmitterType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmitterRepositoryTest {

    @Autowired
    private EmitterTypeRepository emitterTypeRepository;

    @Autowired
    private EmitterOwnerRepository emitterOwnerRepository;

    @Autowired
    private EmitterRepository emitterRepository;

    private final EmitterType emitterType = EmitterType.builder().name("Type 1").build();
    private final EmitterOwner emitterOwner = EmitterOwner.builder().name("Owner 1").build();

    private final Emitter emitterWithInternalGenerator = Emitter.builder()
            .emitterType(emitterType)
            .emitterOwner(emitterOwner)
            .factoryNumber("Emitter_1_Factory_Number")
            .manufactureDate(LocalDate.now())
            .verificationPeriodicityInMonths(12)
            .forExternalUse(false)
            .hasInternalGenerator(true)
            .minimumPulseWidth(20.0)
            .maximumPulseWidth(100.0)
            .minimumPulsePower(100.0)
            .maximumPulsePower(1000.0)
            .minimumRadiationFluxDivergenceAngle(8.0)
            .maximumRadiationFluxDivergenceAngle(12.0)
            .minimumNonParallelismOfTheOpticalAndConstructionAxis(8.0)
            .maximumNonParallelismOfTheOpticalAndConstructionAxis(12.0)
            .minimumUnevennessOfRadiationFLux(30.0)
            .maximumUnevennessOfRadiationFLux(70.0)
            .build();

    Emitter emitterWithoutInternalGenerator = Emitter.builder()
            .emitterType(emitterType)
            .emitterOwner(emitterOwner)
            .factoryNumber("Emitter_2_Factory_Number")
            .manufactureDate(LocalDate.now())
            .verificationPeriodicityInMonths(12)
            .forExternalUse(false)
            .hasInternalGenerator(false)
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
    void givenTwoStoredEmitters_whenFindAll_thenListOfTwoEmittersReturned() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        List<Emitter> savedEmittersList = emitterRepository.saveAll(List.of(emitterWithInternalGenerator, emitterWithoutInternalGenerator));

        // when
        List<Emitter> obtainedEmitters = emitterRepository.findAll();

        // then
        assertNotNull(obtainedEmitters);
        assertEquals(savedEmittersList.size(), obtainedEmitters.size());
    }

    @Test
    @DisplayName("Test get emitter by id functionality (success)")
    void givenSavedEmitter_whenFindById_thenEmitterIsReturned() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        Emitter savedEmitter = emitterRepository.save(emitterWithInternalGenerator);

        // when
        Emitter obtainedEmitter = emitterRepository.findById(emitterWithInternalGenerator.getId()).orElse(null);

        // then
        assertNotNull(obtainedEmitter);
        assertEquals(savedEmitter.getFactoryNumber(), obtainedEmitter.getFactoryNumber());
    }

    @Test
    @DisplayName("Test get emitter by id functionality (not found)")
    void givenEmitterIsNotCreated_whenFindById_thenEmptyOptionalIsReturned() {
        // given
        emitterWithInternalGenerator.setId(1L);

        // when
        Emitter obtainedEmitter = emitterRepository.findById(emitterWithInternalGenerator.getId()).orElse(null);

        // then
        assertNull(obtainedEmitter);
    }

    @Test
    @DisplayName("Test save emitter with internal generator functionality (success)")
    void givenEmitterObjectToSave_whenSave_thenEmitterIsCreated() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        emitterRepository.save(emitterWithInternalGenerator);

        // when
        Emitter createdEmitter = emitterRepository.findById(emitterWithInternalGenerator.getId()).orElse(null);

        // then
        assertNotNull(createdEmitter);
        assertEquals(emitterWithInternalGenerator.getId(), createdEmitter.getId());
        assertEquals(emitterWithInternalGenerator.getFactoryNumber(), createdEmitter.getFactoryNumber());
    }

    @Test
    @DisplayName("Test save emitter with internal generator functionality (constraint exception: value of minimumPulseFrequency10 is not null)")
    void givenEmitterWithInternalGeneratorObjectToSave_whenSave_thenBadRequestExceptionIsThrown() {
        // given
        emitterWithInternalGenerator.setMinimumPulseFrequency10(8.0);

        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterRepository.save(emitterWithInternalGenerator));

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Для излучателя с внутренним генератором поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\" должно быть пустым!",
                exception.getMessage());
    }

    @Test
    @DisplayName("Test save emitter without internal generator functionality (success)")
    void givenEmitterWithoutInternalGeneratorObjectToSave_whenSave_thenEmitterIsCreated() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        emitterRepository.save(emitterWithoutInternalGenerator);

        // when
        Emitter createdEmitter = emitterRepository.findById(emitterWithoutInternalGenerator.getId()).orElse(null);

        // then
        assertNotNull(createdEmitter);
        assertEquals(emitterWithoutInternalGenerator.getId(), createdEmitter.getId());
        assertEquals(emitterWithoutInternalGenerator.getFactoryNumber(), createdEmitter.getFactoryNumber());
    }

    @Test
    @DisplayName("Test save emitter without internal generator functionality (constraint exception: value of minimumPulseFrequency10 is not null)")
    void givenEmitterWithoutInternalGeneratorObjectToSave_whenSave_thenBadRequestExceptionIsThrown() {
        // given
        emitterWithoutInternalGenerator.setMinimumPulseFrequency10(null);

        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterRepository.save(emitterWithoutInternalGenerator));

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Заполните поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\"",
                exception.getMessage());
    }

    @Test
    @DisplayName("Test update emitter with internal generator functionality (success)")
    void givenEmitterWithInternalGeneratorObjectToUpdate_whenSave_thenEmitterIsUpdated() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        String updatedFactoryNumber = "UPDATED_Emitter_1_Factory_Number";
        Emitter savedEmitter = emitterRepository.save(emitterWithInternalGenerator);

        // when
        Emitter obtainedEmitter = emitterRepository.findById(savedEmitter.getId()).orElse(null);
        assert obtainedEmitter != null;
        obtainedEmitter.setFactoryNumber(updatedFactoryNumber);

        Emitter updatedEmitter = emitterRepository.save(obtainedEmitter);

        // then
        assertNotNull(updatedEmitter);
        assertEquals(savedEmitter.getId(), updatedEmitter.getId());
        assertEquals(updatedFactoryNumber, updatedEmitter.getFactoryNumber());
    }

    @Test
    @DisplayName("Test update emitter with internal generator functionality (constraint exception: value of minimumPulseFrequency10 is not null)")
    void givenEmitterWithInternalGeneratorObjectToUpdate_whenSave_thenBadRequestExceptionIsThrown() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        Emitter savedEmitter = emitterRepository.save(emitterWithInternalGenerator);
        savedEmitter.setMinimumPulseFrequency10(8.0);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterRepository.saveAndFlush(savedEmitter));

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Для излучателя с внутренним генератором поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\" должно быть пустым!",
                exception.getMessage());
    }

    @Test
    @DisplayName("Test update emitter without internal generator functionality (success)")
    void givenEmitterWithoutInternalGeneratorObjectToUpdate_whenSave_thenEmitterIsUpdated() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        String updatedFactoryNumber = "UPDATED_Emitter_1_Factory_Number";
        Emitter savedEmitter = emitterRepository.save(emitterWithoutInternalGenerator);

        // when
        Emitter obtainedEmitter = emitterRepository.findById(savedEmitter.getId()).orElse(null);
        assert obtainedEmitter != null;
        obtainedEmitter.setFactoryNumber(updatedFactoryNumber);

        Emitter updatedEmitter = emitterRepository.save(obtainedEmitter);

        // then
        assertNotNull(updatedEmitter);
        assertEquals(savedEmitter.getId(), updatedEmitter.getId());
        assertEquals(updatedFactoryNumber, updatedEmitter.getFactoryNumber());
    }

    @Test
    @DisplayName("Test update emitter without internal generator functionality (constraint exception: value of minimumPulseFrequency10 is not null)")
    void givenEmitterWithoutInternalGeneratorObjectToUpdate_whenSave_thenBadRequestExceptionIsThrown() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);

        Emitter savedEmitter = emitterRepository.save(emitterWithoutInternalGenerator);
        savedEmitter.setMinimumPulseFrequency10(null);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterRepository.saveAndFlush(savedEmitter));

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Заполните поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\"",
                exception.getMessage());
    }

    @Test
    @DisplayName("Test delete emitter by id functionality")
    void givenSavedEmitter_whenDeleteById_thenEmitterIsRemovedFromDatabase() {
        // given
        emitterTypeRepository.save(emitterType);
        emitterOwnerRepository.save(emitterOwner);
        Emitter savedEmitter = emitterRepository.save(emitterWithInternalGenerator);

        // when
        emitterRepository.deleteById(savedEmitter.getId());

        // then
        Emitter obtainedEmitter = emitterRepository.findById(savedEmitter.getId()).orElse(null);
        assertNull(obtainedEmitter);
    }
}
