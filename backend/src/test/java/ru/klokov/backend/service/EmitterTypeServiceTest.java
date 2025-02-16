package ru.klokov.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.repository.EmitterTypeRepository;
import ru.klokov.backend.service.implementation.DefaultEmitterTypeService;

@ExtendWith(MockitoExtension.class)
class EmitterTypeServiceTest {

    @Mock
    private EmitterTypeRepository emitterTypeRepository;

    @InjectMocks
    private DefaultEmitterTypeService emitterTypeService;

    private EmitterType emitterType;

    private ApiException notFoundException = new ApiException(HttpStatus.NOT_FOUND);
    private ApiException conflictException = new ApiException(HttpStatus.CONFLICT);

    @BeforeEach
    public void setUp() {
        emitterType = EmitterType.builder().id(1L).name("Type 1").build();
    }

    @Test
    @DisplayName("Test get all emitter types functionality")
    void givenThreeEmitterTypes_whenGetAll_thenListOfThreeEmitterTypesReturned() {
        // given
        EmitterType secondEmitterType = EmitterType.builder().id(2L).name("Type 2").build();
        EmitterType thirdEmitterType = EmitterType.builder().id(3L).name("Type 3").build();

        given(emitterTypeRepository.findAll()).willReturn(List.of(emitterType, secondEmitterType, thirdEmitterType));

        // when
        List<EmitterType> obtainedEmitterTypes = emitterTypeService.getAllEmitterTypes();

        // then
        assertNotNull(obtainedEmitterTypes);
        assertEquals(3, obtainedEmitterTypes.size());
    }

    @Test
    @DisplayName("Test get emitter type by id functionality (success)")
    void givenId_whenGetById_thenEmitterTypeIsReturned() {
        // given
        given(emitterTypeRepository.findById(anyLong())).willReturn(Optional.of(emitterType));

        // when
        EmitterType obtainedEmitterType = emitterTypeService.getEmitterTypeById(1L);

        // then
        assertNotNull(obtainedEmitterType);
        assertEquals(emitterType.getId(), obtainedEmitterType.getId());
    }

    @Test
    @DisplayName("Test get emitter type by id functionality (not found)")
    void givenId_whenGetById_thenNotFoundExceptionIsThrown() {
        // given
        given(emitterTypeRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> {
            emitterTypeService.getEmitterTypeById(1L);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Test get page of emitter types functionality")
    void givenPageParameters_whenGetPage_thenPageOfEmitterTypesIsReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        Page<EmitterType> mockPage = new PageImpl<>(List.of(emitterType));

        // when
        given(emitterTypeRepository.findAll(pageable)).willReturn(mockPage);

        Page<EmitterType> emitterTypesPage = emitterTypeService.getEmitterTypesPage(1, 5, "id", true);

        // then
        assertNotNull(emitterTypesPage);
        assertEquals(1, emitterTypesPage.getContent().size());
    }

    @Test
    @DisplayName("Test create emitter type functionality (success)")
    void givenEmitterTypeToCreate_whenCreateEmitterType_thenCreatedEmitterTypeIsReturned() {
        // given
        given(emitterTypeRepository.save(any(EmitterType.class))).willReturn(emitterType);

        // when
        EmitterType createdEmitterType = emitterTypeService.createEmitterType(emitterType);

        // then
        assertNotNull(createdEmitterType);
        assertEquals(1L, createdEmitterType.getId());
    }

    @Test
    @DisplayName("Test create emitter type functionality (conflict)")
    void givenEmitterTypeToCreate_whenCreateEmitterType_thenConflictExceptionIsThrown() {
        // given
        given(emitterTypeRepository.save(any(EmitterType.class))).willThrow(conflictException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> {
            emitterTypeService.createEmitterType(emitterType);
        });

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("Test update emitter type functionality (success)")
    void givenEmitterTypeAndId_whenUpdate_thenUpdatedEmitterTypeIsReturned() {
        // given
        EmitterType updatedEmitterType = EmitterType.builder().id(1L).name("Updated Type 1").build();

        given(emitterTypeRepository.findById(anyLong())).willReturn(Optional.of(emitterType));
        given(emitterTypeRepository.save(any(EmitterType.class))).willReturn(updatedEmitterType);

        // when
        emitterTypeService.updateEmitterType(emitterType.getId(), updatedEmitterType);

        // then
        assertNotNull(updatedEmitterType);
        assertEquals(1L, updatedEmitterType.getId());
    }

    @Test
    @DisplayName("Test update emitter type functionality (not found)")
    void givenEmitterTypeAndId_whenUpdate_thenNotFoundExceptionIsThrown() {
        // given
        EmitterType updatedEmitterType = EmitterType.builder().id(1L).name("Updated Type 1").build();
        Long emitterTypeToUpdateId = emitterType.getId();

        given(emitterTypeRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> {
            emitterTypeService.updateEmitterType(emitterTypeToUpdateId, updatedEmitterType);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Test update emitter type functionality (conflict)")
    void givenEmitterTypeAndId_whenUpdate_thenConflictExceptionIsThrown() {
        // given
        EmitterType updatedEmitterType = EmitterType.builder().id(1L).name("Type 1").build();
        Long emitterTypeToUpdateId = emitterType.getId();

        given(emitterTypeRepository.findById(anyLong())).willReturn(Optional.of(emitterType));
        given(emitterTypeRepository.save(any(EmitterType.class))).willThrow(conflictException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> {
            emitterTypeService.updateEmitterType(emitterTypeToUpdateId, updatedEmitterType);
        });

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("Test delete by id functionality (success)")
    void givenId_whenDeleteById_thenDeleteMethodOfRepositoryIsCalled() {
        // given
        given(emitterTypeRepository.findById(anyLong())).willReturn(Optional.of(emitterType));

        // when
        emitterTypeService.deleteEmitterType(emitterType.getId());

        // then
        verify(emitterTypeRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test delete by id functionality (not found)")
    void givenId_whenDeleteById_thenExceptionIsThrown() {
        // given
        Long emitterTypeToDeleteId = emitterType.getId();

        given(emitterTypeRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> {
            emitterTypeService.deleteEmitterType(emitterTypeToDeleteId);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}