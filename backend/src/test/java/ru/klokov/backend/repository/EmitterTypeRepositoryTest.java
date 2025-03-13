package ru.klokov.backend.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.klokov.backend.model.EmitterType;

import java.util.List;

@DataJpaTest
class EmitterTypeRepositoryTest {

    @Autowired
    private EmitterTypeRepository emitterTypeRepository;

    @BeforeEach
    void setUp() {
        emitterTypeRepository.deleteAll();
    }

    @Test
    @DisplayName("Test get all emitter types functionality")
    void givenThreeStoredEmitterType_whenFindAll_thenListOfThreeEmitterTypesReturned() {
        // given
        EmitterType emitterType1 = EmitterType.builder().name("Type 1").build();
        EmitterType emitterType2 = EmitterType.builder().name("Type 2").build();
        EmitterType emitterType3 = EmitterType.builder().name("Type 3").build();

        List<EmitterType> savedEmitterTypesList = emitterTypeRepository.saveAll(List.of(emitterType1, emitterType2, emitterType3));
        // when
        List<EmitterType> obtainedEmitterTypes = emitterTypeRepository.findAll();

        // then
        Assertions.assertNotNull(obtainedEmitterTypes);
        Assertions.assertEquals(savedEmitterTypesList.size(), obtainedEmitterTypes.size());
    }

    @Test
    @DisplayName("Test get emitter type by id functionality")
    void givenSavedEmitterType_whenFindById_thenEmitterTypeIsReturned() {
        // given
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();

        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);
        // when
        EmitterType obtainedEmitterType = emitterTypeRepository.findById(emitterType.getId()).orElse(null);

        // then
        Assertions.assertNotNull(obtainedEmitterType);
        Assertions.assertEquals(savedEmitterType.getName(), obtainedEmitterType.getName());
    }

    @Test
    @DisplayName("Test emitter type not found functionality")
    void givenEmitterTypeIsNotCreated_whenFindById_thenEmptyOptionalIsRetunred() {
        // given
        EmitterType emitterType = EmitterType.builder().id(1L).name("Type 1").build();

        // when
        EmitterType obtainedEmitterType = emitterTypeRepository.findById(emitterType.getId()).orElse(null);

        // then
        Assertions.assertNull(obtainedEmitterType);
    }

    @Test
    @DisplayName("Test save emitter type functionality")
    void givenEmitterTypeObjectToSave_whenSave_thenEmitterTypeIsCreated() {
        // given
        EmitterType emitterTypeToSave = EmitterType.builder().id(1L).name("Type 1").build();

        emitterTypeRepository.save(emitterTypeToSave);

        // when
        EmitterType createdEmitterType = emitterTypeRepository.findById(emitterTypeToSave.getId()).orElse(null);

        // then
        Assertions.assertNotNull(createdEmitterType);
        Assertions.assertEquals(emitterTypeToSave.getId(), createdEmitterType.getId());
    }

    @Test
    @DisplayName("Test update emitter type functionality")
    void givenEmitterTypeObjectToUpdate_whenSave_thenEmitterTypeIsUpdated() {
        // given
        String updatedName = "Updated type";
        EmitterType emitterTypeToUpdate = EmitterType.builder().name("Type 1").build();
        EmitterType savedEmitterType = emitterTypeRepository.save(emitterTypeToUpdate);

        // when
        EmitterType obtainedEmitterType = emitterTypeRepository.findById(savedEmitterType.getId()).orElse(null);
        assert obtainedEmitterType != null;
        obtainedEmitterType.setName(updatedName);

        EmitterType updatedEmitterType = emitterTypeRepository.save(obtainedEmitterType);

        // then
        Assertions.assertNotNull(updatedEmitterType);
        Assertions.assertEquals(updatedName, updatedEmitterType.getName());
    }

    @Test
    @DisplayName("Test delete emitter type by id functionality")
    void givenSavedEmitterType_whenDeleteById_thenEmitterTypeIsRemovedFromDatabase() {
        // given
        EmitterType emitterType = EmitterType.builder().id(1L).name("Type 1").build();
        
        emitterTypeRepository.save(emitterType);
        
        // when
        emitterTypeRepository.deleteById(emitterType.getId());

        // then
        EmitterType obtainedEmitterType = emitterTypeRepository.findById(emitterType.getId()).orElse(null);
        Assertions.assertNull(obtainedEmitterType);
    }   
}
