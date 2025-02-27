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
import ru.klokov.backend.model.Emitter;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.repository.EmitterRepository;
import ru.klokov.backend.service.implementation.DefaultEmitterService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmitterServiceTest {

    @Mock
    private EmitterRepository emitterRepository;

    @InjectMocks
    private DefaultEmitterService emitterService;

    private Emitter emitter;

    private final ApiException notFoundException = new ApiException(HttpStatus.NOT_FOUND);
    private final ApiException conflictException = new ApiException(HttpStatus.CONFLICT);

    @BeforeEach
    public void setUp() {
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();
        EmitterOwner emitterOwner = EmitterOwner.builder().name("Owner 1").build();

        emitter = Emitter.builder()
                .emitterType(emitterType)
                .emitterOwner(emitterOwner)
                .factoryNumber("Factory number 1")
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
    }

    @Test
    @DisplayName("Test get all emitters functionality")
    void givenThreeEmitters_whenGetAll_thenListOfThreeEmittersReturned() {
        // given
        int emittersNumber = 1;

        given(emitterRepository.findAll()).willReturn(List.of(emitter));

        // when
        List<Emitter> obtainedEmitters = emitterService.getAllEmitters();

        // then
        assertNotNull(obtainedEmitters);
        assertEquals(emittersNumber, obtainedEmitters.size());
    }

    @Test
    @DisplayName("Test get emitter by id functionality (success)")
    void givenId_whenGetById_thenEmitterIsReturned() {
        // given
        Emitter emitterToGet = emitter;
        emitterToGet.setId(1L);

        given(emitterRepository.findById(anyLong())).willReturn(Optional.of(emitterToGet));

        // when
        Emitter obtainedEmitter = emitterService.getEmitterById(emitterToGet.getId());

        // then
        assertNotNull(obtainedEmitter);
        assertEquals(emitterToGet.getId(), obtainedEmitter.getId());
    }

    @Test
    @DisplayName("Test get emitter by id functionality (not found)")
    void givenId_whenGetById_thenNotFoundExceptionIsThrown() {
        // given
        given(emitterRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterService.getEmitterById(1L));

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Test get page of emitter functionality")
    void givenPageParameters_whenGetPage_thenPageOfEmittersIsReturned() {
        // given
        int emittersNumber = 1;

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        Page<Emitter> mockPage = new PageImpl<>(List.of(emitter));

        // when
        given(emitterRepository.findAll(pageable)).willReturn(mockPage);

        Page<Emitter> emitterPage = emitterService.getEmittersPage(1, 5, "id", true);

        // then
        assertNotNull(emitterPage);
        assertEquals(emittersNumber, emitterPage.getContent().size());
    }

    @Test
    @DisplayName("Test create emitter functionality (success)")
    void givenEmitterToCreate_whenCreateEmitter_thenCreatedEmitterIsReturned() {
        // given
        Emitter emitterToCreate = emitter;
        emitterToCreate.setId(1L);

        given(emitterRepository.save(any(Emitter.class))).willReturn(emitter);

        // when
        Emitter createdEmitter = emitterService.createEmitter(emitterToCreate);

        // then
        assertNotNull(createdEmitter);
        assertEquals(emitterToCreate.getId(), createdEmitter.getId());
    }

    @Test
    @DisplayName("Test create emitter functionality (conflict)")
    void givenEmitterToCreate_whenCreateEmitter_thenConflictExceptionIsThrown() {
        // given
        given(emitterRepository.save(any(Emitter.class))).willThrow(conflictException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterService.createEmitter(emitter));

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("Test update emitter functionality (success)")
    void givenEmitterAndId_whenUpdate_thenUpdatedEmitterIsReturned() {
        // given
        Emitter emitterToUpdate = emitter;
        emitterToUpdate.setId(1L);

        String updatedFactoryNumber = "UpdatedFactoryNumber";
        Emitter updatedEmitter = emitterToUpdate;
        updatedEmitter.setFactoryNumber(updatedFactoryNumber);

        given(emitterRepository.findById(anyLong())).willReturn(Optional.of(emitterToUpdate));
        given(emitterRepository.save(any(Emitter.class))).willReturn(updatedEmitter);

        // when
        emitterService.updateEmitter(emitterToUpdate.getId(), updatedEmitter);

        // then
        assertNotNull(updatedEmitter);
        assertEquals(emitterToUpdate.getId(), updatedEmitter.getId());
        assertEquals(updatedFactoryNumber, updatedEmitter.getFactoryNumber());
    }

    @Test
    @DisplayName("Test update emitter functionality (not found)")
    void givenEmitterAndId_whenUpdate_thenNotFoundExceptionIsThrown() {
        // given
        Long emitterToUpdateId = 1L;

        String updatedFactoryNumber = "UpdatedFactoryNumber";
        Emitter updatedEmitter = emitter;
        updatedEmitter.setFactoryNumber(updatedFactoryNumber);

        given(emitterRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterService.updateEmitter(emitterToUpdateId, updatedEmitter));

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Test update emitter functionality (conflict)")
    void givenEmitterAndId_whenUpdate_thenConflictExceptionIsThrown() {
        // given
        Long emitterToUpdateId = 1L;

        String updatedFactoryNumber = "UpdatedFactoryNumber";
        Emitter updatedEmitter = emitter;
        updatedEmitter.setFactoryNumber(updatedFactoryNumber);

        given(emitterRepository.findById(anyLong())).willReturn(Optional.of(emitter));
        given(emitterRepository.save(any(Emitter.class))).willThrow(conflictException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterService.updateEmitter(emitterToUpdateId, updatedEmitter));

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("Test delete emitter by id functionality (success)")
    void givenId_whenDeleteById_thenDeleteMethodOfRepositoryIsCalled() {
        // given
        Long emitterToDeleteId = 1L;

        given(emitterRepository.findById(anyLong())).willReturn(Optional.of(emitter));

        // when
        emitterService.deleteEmitter(emitterToDeleteId);

        // then
        verify(emitterRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test delete emitter by id functionality (not found)")
    void givenId_whenDeleteById_thenExceptionIsThrown() {
        // given
        Long emitterToDeleteId = 1L;

        given(emitterRepository.findById(anyLong())).willThrow(notFoundException);

        // when
        ApiException exception = assertThrows(ApiException.class, () -> emitterService.deleteEmitter(emitterToDeleteId));

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
