package ru.klokov.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.repository.EmitterOwnerRepository;
import ru.klokov.backend.service.implementation.DefaultEmitterOwnerService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmitterOwnerServiceTest {
        
    @Mock
    private EmitterOwnerRepository emitterOwnerRepository;

    @InjectMocks
    private DefaultEmitterOwnerService emitterOwnerService;

    private EmitterOwner emitterOwner;

    private final ApiException notFoundException = new ApiException(HttpStatus.NOT_FOUND);
    private final ApiException conflictException = new ApiException(HttpStatus.CONFLICT);

    @BeforeEach
    public void setUp() {
        emitterOwner = EmitterOwner.builder().id(1L).name("Owner 1").build();
    }

    @Test
    @DisplayName("Test get all emitter owners functionality")
    void givenThreeEmitterOwners_whenGetAll_thenListOfThreeEmitterOwnersReturned() {
        // given
        EmitterOwner secondEmitterOwner = EmitterOwner.builder().id(2L).name("Owner 2").build();
        EmitterOwner thirdEmitterOwner = EmitterOwner.builder().id(3L).name("Owner 3").build();

        given(emitterOwnerRepository.findAll()).willReturn(List.of(emitterOwner, secondEmitterOwner, thirdEmitterOwner));

        // when
        List<EmitterOwner> obtainedEmitterOwners = emitterOwnerService.getAllEmitterOwners();

        // then
        assertNotNull(obtainedEmitterOwners);
        assertEquals(3, obtainedEmitterOwners.size());
    }

    @Test
    @DisplayName("Test get emitter owner by id functionality (success)")
    void givenId_whenGetById_thenEmitterOwnerIsReturned() {
        // given
        given(emitterOwnerRepository.findById(anyLong())).willReturn(Optional.of(emitterOwner));

        // when
        EmitterOwner obtainedEmitterOwner = emitterOwnerService.getEmitterOwnerById(1L);

        // then
        assertNotNull(obtainedEmitterOwner);
        assertEquals(emitterOwner.getId(), obtainedEmitterOwner.getId());
    }

    @Test
    @DisplayName("Test get emitter owner by id functionality (not found)")
    void givenId_whenGetById_thenNotFoundExceptionIsThrown() {
        // given
        given(emitterOwnerRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () ->
            emitterOwnerService.getEmitterOwnerById(1L)
        );

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Test get page of emitter owners functionality")
    void givenPageParameters_whenGetPage_thenPageOfEmitterOwnersIsReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        Page<EmitterOwner> mockPage = new PageImpl<>(List.of(emitterOwner));

        // when
        given(emitterOwnerRepository.findAll(pageable)).willReturn(mockPage);

        Page<EmitterOwner> emitterOwnersPage = emitterOwnerService.getEmitterOwnersPage(1, 5, "id", true);

        // then
        assertNotNull(emitterOwnersPage);
        assertEquals(1, emitterOwnersPage.getContent().size());
    }

    @Test
    @DisplayName("Test create emitter owner functionality (success)")
    void givenEmitterOwnerToCreate_whenCreateEmitterOwner_thenCreatedEmitterOwnerIsReturned() {
        // given
        given(emitterOwnerRepository.save(any(EmitterOwner.class))).willReturn(emitterOwner);

        // when
        EmitterOwner createdEmitterOwner = emitterOwnerService.createEmitterOwner(emitterOwner);

        // then
        assertNotNull(createdEmitterOwner);
        assertEquals(1L, createdEmitterOwner.getId());
    }

    @Test
    @DisplayName("Test create emitter owner functionality (conflict)")
    void givenEmitterOwnerToCreate_whenCreateEmitterOwner_thenConflictExceptionIsThrown() {
        // given
        given(emitterOwnerRepository.save(any(EmitterOwner.class))).willThrow(conflictException);

        // when
        ApiException exception = assertThrows(ApiException.class, () ->
            emitterOwnerService.createEmitterOwner(emitterOwner)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("Test update emitter owner functionality (success)")
    void givenEmitterOwnerAndId_whenUpdate_thenUpdatedEmitterOwnerIsReturned() {
        // given
        EmitterOwner updatedEmitterOwner = EmitterOwner.builder().id(1L).name("Updated Owner 1").build();

        given(emitterOwnerRepository.findById(anyLong())).willReturn(Optional.of(emitterOwner));
        given(emitterOwnerRepository.save(any(EmitterOwner.class))).willReturn(updatedEmitterOwner);

        // when
        emitterOwnerService.updateEmitterOwner(emitterOwner.getId(), updatedEmitterOwner);

        // then
        assertNotNull(updatedEmitterOwner);
        assertEquals(1L, updatedEmitterOwner.getId());
    }

    @Test
    @DisplayName("Test update emitter owner functionality (not found)")
    void givenEmitterOwnerAndId_whenUpdate_thenNotFoundExceptionIsThrown() {
        // given
        EmitterOwner updatedEmitterOwner = EmitterOwner.builder().id(1L).name("Updated Owner 1").build();
        Long emitterOwnerToUpdateId = emitterOwner.getId();

        given(emitterOwnerRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () ->
            emitterOwnerService.updateEmitterOwner(emitterOwnerToUpdateId, updatedEmitterOwner)
        );

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Test update emitter owner functionality (conflict)")
    void givenEmitterOwnerAndId_whenUpdate_thenConflictExceptionIsThrown() {
        // given
        EmitterOwner updatedEmitterOwner = EmitterOwner.builder().id(1L).name("Owner 1").build();
        Long emitterOwnerToUpdateId = emitterOwner.getId();

        given(emitterOwnerRepository.findById(anyLong())).willReturn(Optional.of(emitterOwner));
        given(emitterOwnerRepository.save(any(EmitterOwner.class))).willThrow(conflictException);

        // when
        ApiException exception = assertThrows(ApiException.class, () ->
            emitterOwnerService.updateEmitterOwner(emitterOwnerToUpdateId, updatedEmitterOwner)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("Test delete by id functionality (success)")
    void givenId_whenDeleteById_thenDeleteMethodOfRepositoryIsCalled() {
        // given
        given(emitterOwnerRepository.findById(anyLong())).willReturn(Optional.of(emitterOwner));

        // when
        emitterOwnerService.deleteEmitterOwner(emitterOwner.getId());

        // then
        verify(emitterOwnerRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test delete by id functionality (not found)")
    void givenId_whenDeleteById_thenExceptionIsThrown() {
        // given
        Long emitterOwnerToDeleteId = emitterOwner.getId();

        given(emitterOwnerRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () ->
            emitterOwnerService.deleteEmitterOwner(emitterOwnerToDeleteId)
        );

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
