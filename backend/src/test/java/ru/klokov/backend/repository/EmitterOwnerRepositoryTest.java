package ru.klokov.backend.repository;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ru.klokov.backend.model.EmitterOwner;

@DataJpaTest
class EmitterOwnerRepositoryTest {

    @Autowired
    private EmitterOwnerRepository emitterOwnerRepository;

    @BeforeEach
    void setUp() {
        emitterOwnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test get all emitter owners functionality")
    void givenThreeStoredEmitterOwners_whenFindAll_thenListOfThreeEmitterOwnersReturned() {
        // given
        EmitterOwner emitterOwner1 = EmitterOwner.builder().name("Owner 1").build();
        EmitterOwner emitterOwner2 = EmitterOwner.builder().name("Owner 2").build();
        EmitterOwner emitterOwner3 = EmitterOwner.builder().name("Owner 3").build();

        List<EmitterOwner> savedEmitterOwnersList = emitterOwnerRepository.saveAll(List.of(emitterOwner1, emitterOwner2, emitterOwner3));
        // when
        List<EmitterOwner> obtainedEmitterOwners = emitterOwnerRepository.findAll();

        // then
        assertNotNull(obtainedEmitterOwners);
        assertEquals(savedEmitterOwnersList.size(), obtainedEmitterOwners.size());
    }

    @Test
    @DisplayName("Test get emitter owner by id functionality")
    void givenSavedEmitterOwner_whenFindById_thenEmitterOwnerIsReturned() {
        // given
        EmitterOwner emitterOwner = EmitterOwner.builder().name("Owner 1").build();

        EmitterOwner savedEmitterOwner = emitterOwnerRepository.save(emitterOwner);
        // when
        EmitterOwner obtainedEmitterOwner = emitterOwnerRepository.findById(emitterOwner.getId()).orElse(null);

        // then
        assertNotNull(obtainedEmitterOwner);
        assertEquals(savedEmitterOwner.getName(), obtainedEmitterOwner.getName());
    }

    @Test
    @DisplayName("Test emitter owner not found functionality")
    void givenEmitterOwnerIsNotCreated_whenFindById_thenEmptyOptionalIsRetunred() {
        // given
        EmitterOwner emitterOwner = EmitterOwner.builder().id(1L).name("Owner 1").build();

        // when
        EmitterOwner obtainedEmitterOwner = emitterOwnerRepository.findById(emitterOwner.getId()).orElse(null);

        // then
        assertNull(obtainedEmitterOwner);
    }

    @Test
    @DisplayName("Test save emitter owner functionality")
    void givenEmitterOwnerObjectToSave_whenSave_thenEmitterOwnerIsCreated() {
        // given
        EmitterOwner emitterOwnerToSave = EmitterOwner.builder().id(1L).name("Owner 1").build();

        emitterOwnerRepository.save(emitterOwnerToSave);

        // when
        EmitterOwner createdEmitterOwner = emitterOwnerRepository.findById(emitterOwnerToSave.getId()).orElse(null);

        // then
        assertNotNull(createdEmitterOwner);
        assertEquals(emitterOwnerToSave.getId(), createdEmitterOwner.getId());
    }

    @Test
    @DisplayName("Test update emitter owner functionality")
    void givenEmitterOwnerObjectToUpdate_whenSave_thenEmitterOwnerIsUpdated() {
        // given
        String updatedName = "Updated owner";
        EmitterOwner emitterOwnerToUpdate = EmitterOwner.builder().id(1L).name("Owner 1").build();
        EmitterOwner savedEmitterOwner = emitterOwnerRepository.save(emitterOwnerToUpdate);

        // when
        EmitterOwner obtainedEmitterOwner = emitterOwnerRepository.findById(savedEmitterOwner.getId()).orElse(null);
        obtainedEmitterOwner.setName(updatedName);

        EmitterOwner updatedEmitterOwner = emitterOwnerRepository.save(obtainedEmitterOwner);

        // then
        assertNotNull(updatedEmitterOwner);
        assertEquals(updatedName, updatedEmitterOwner.getName());
    }

    @Test
    @DisplayName("Test delete emitter owner by id functionality")
    void givenSavedEmitterOwner_whenDeleteById_thenEmitterOwnerIsRemovedFromDatabase() {
        // given
        EmitterOwner emitterOwner = EmitterOwner.builder().id(1L).name("Owner 1").build();
        
        emitterOwnerRepository.save(emitterOwner);
        
        // when
        emitterOwnerRepository.deleteById(emitterOwner.getId());

        // then
        EmitterOwner obtainedEmitterOwner = emitterOwnerRepository.findById(emitterOwner.getId()).orElse(null);
        assertNull(obtainedEmitterOwner);
    }
}
