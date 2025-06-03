package ru.klokov.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import ru.klokov.backend.model.EmitterType;

@DataJpaTest
class EmitterTypeRepositoryTest {

    @Autowired
    private EmitterTypeRepository emitterTypeRepository;

    @Test
    @DisplayName("Test get all emitter types functionality")
    void givenThreeStoredEmitterTypes_whenFindAll_thenListOfThreeEmitterTypesReturned() {
        // given
        String name1 = "Type 1";
        String name2 = "Type 2";
        String name3 = "Type 3";

        EmitterType emitterType1 = EmitterType.builder().name(name1).build();
        EmitterType emitterType2 = EmitterType.builder().name(name2).build();
        EmitterType emitterType3 = EmitterType.builder().name(name3).build();

        emitterTypeRepository.saveAll(List.of(emitterType1, emitterType2, emitterType3));

        // when
        List<EmitterType> obtainedEmitterTypes = emitterTypeRepository.findAll();

        // then
        assertThat(obtainedEmitterTypes)
                .isNotEmpty()
                .hasSize(3)
                .extracting(EmitterType::getName)
                .containsExactlyInAnyOrder(name1, name2, name3);
    }

    @Test
    @DisplayName("Test get emitter type by id functionality (success)")
    void givenSavedEmitterType_whenFindById_thenEmitterTypeIsReturned() {
        // given
        String name = "Type 1";
        EmitterType emitterType = EmitterType.builder().name(name).build();

        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);
        Long id = savedEmitterType.getId();

        // when
        EmitterType obtainedEmitterType = emitterTypeRepository.findById(id).orElse(null);

        // then
        assertThat(obtainedEmitterType).isNotNull();
        assertThat(obtainedEmitterType.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Test get emitter type by id functionality (not found)")
    void givenEmitterTypeIsNotCreated_whenFindById_thenEmptyOptionalIsRetunred() {
        // given
        Long nonExistedId = Long.MAX_VALUE;

        // when
        EmitterType obtainedEmitterType = emitterTypeRepository.findById(nonExistedId).orElse(null);

        // then
        assertThat(obtainedEmitterType).isNull();
    }

    @Test
    @DisplayName("Test save emitter type functionality (success)")
    void givenEmitterTypeObjectToSave_whenSave_thenEmitterTypeIsCreated() {
        // given
        String name = "Type 1";
        EmitterType emitterTypeToSave = EmitterType.builder().name(name).build();

        EmitterType savedEmitterType = emitterTypeRepository.save(emitterTypeToSave);
        Long savedEmitterTypeId = savedEmitterType.getId();

        // when
        EmitterType createdEmitterType = emitterTypeRepository.findById(savedEmitterTypeId).orElse(null);

        // then
        assertThat(savedEmitterTypeId).isNotNull();
        assertThat(createdEmitterType).isNotNull();
        assertThat(createdEmitterType.getId()).isEqualTo(savedEmitterTypeId);
        assertThat(createdEmitterType.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Test save emitter type functionality (duplicate name)")
    void givenEmitterTypeObjectToSave_whenSaveDuplicateName_thenThrowException() {
        // given
        String duplicateName = "DuplicateEmitterTypeName";
        EmitterType emitterType1 = EmitterType.builder().name(duplicateName).build();

        emitterTypeRepository.save(emitterType1);

        EmitterType emitterType2 = EmitterType.builder().name(duplicateName).build();

        // when - then
        assertThatThrownBy(() -> emitterTypeRepository.saveAndFlush(emitterType2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Test save emitter type functionality (null name)")
    void givenEmitterTypeObjectToSave_whenSaveNullName_thenThrowException() {
        // given
        EmitterType emitterType = EmitterType.builder().name(null).build();

        // when - then
        assertThatThrownBy(() -> emitterTypeRepository.saveAndFlush(emitterType))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Test update emitter type functionality (success)")
    void givenEmitterTypeObjectToUpdate_whenSave_thenEmitterTypeIsUpdated() {
        // given
        String name = "Type 1";
        String updatedName = "Updated type 1";

        EmitterType emitterTypeToUpdate = EmitterType.builder().name(name).build();
        EmitterType savedEmitterType = emitterTypeRepository.save(emitterTypeToUpdate);

        // when
        EmitterType obtainedEmitterType = emitterTypeRepository.findById(savedEmitterType.getId()).orElse(null);

        assertThat(obtainedEmitterType).isNotNull();

        obtainedEmitterType.setName(updatedName);

        EmitterType updatedEmitterType = emitterTypeRepository.saveAndFlush(obtainedEmitterType);

        // then
        assertThat(updatedEmitterType).isNotNull();
        assertThat(updatedEmitterType.getName()).isEqualTo(updatedName);

        // verify update persisted
        EmitterType refreshedEmitterType = emitterTypeRepository.findById(updatedEmitterType.getId()).orElse(null);
        assertThat(refreshedEmitterType).isNotNull();
        assertThat(refreshedEmitterType.getName()).isEqualTo(updatedName);
    }

    @Test
    @DisplayName("Test update emitter type functionality (duplicate name)")
    void givenEmitterTypeObjectToUpdate_whenUpdateWithDuplicateName_thenThrowException() {
        // given
        String name1 = "Type1";
        String name2 = "Type2";

        EmitterType emitterType1 = EmitterType.builder().name(name1).build();
        EmitterType emitterType2 = EmitterType.builder().name(name2).build();

        emitterTypeRepository.save(emitterType1);
        EmitterType savedEmitterType2 = emitterTypeRepository.save(emitterType2);

        // when - then
        savedEmitterType2.setName(name1);

        assertThatThrownBy(() -> emitterTypeRepository.saveAndFlush(savedEmitterType2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Test update emitter type functionality (null name)")
    void givenEmitterTypeObjectToUpdate_whenUpdateWithNullName_thenThrowException() {
        // given
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();
        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);

        // when
        savedEmitterType.setName(null);

        assertThatThrownBy(() -> emitterTypeRepository.saveAndFlush(savedEmitterType))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Test delete emitter type by id functionality (success)")
    void givenSavedEmitterType_whenDeleteById_thenEmitterTypeIsRemovedFromDatabase() {
        // given
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();
        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);
        Long id = savedEmitterType.getId();

        assertThat(emitterTypeRepository.findById(id)).isPresent();
        // when
        emitterTypeRepository.deleteById(id);

        // then
        assertThat(emitterTypeRepository.findById(id)).isNotPresent();
    }

    @DisplayName("Test delete emitter type by id functionality (non-existent id)")
    void givenNonExistentId_whenDeleteById_thenNothingHappens() {
        // given
        Long nonExistentId = 999L;

        // when
        emitterTypeRepository.deleteById(nonExistentId);

        // then
        assertThat(emitterTypeRepository.findById(nonExistentId)).isNotPresent();
    }

    @Test
    @DisplayName("Test delete emitter type by id functionality (null id)")
    void givenNullId_whenDeleteById_thenExceptionIsThrown() {
        // given
        Long nullId = null;

        // when - then
        assertThatThrownBy(() -> emitterTypeRepository.deleteById(nullId))
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessageContaining("must not be null");
    }
}