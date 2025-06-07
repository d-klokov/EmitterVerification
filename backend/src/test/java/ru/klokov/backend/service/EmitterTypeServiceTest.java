package ru.klokov.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import ru.klokov.backend.exception.ServerException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.repository.EmitterTypeRepository;
import ru.klokov.backend.service.implementation.DefaultEmitterTypeService;

@ExtendWith(MockitoExtension.class)
public class EmitterTypeServiceTest {

    @Mock
    private EmitterTypeRepository emitterTypeRepository;

    @InjectMocks
    private DefaultEmitterTypeService emitterTypeService;

    @Test
    @DisplayName("Test get all emitter types functionality")
    void givenTwoEmitterTypes_getAllEmitterTypes_ShouldReturnListOfEmitterTypes() {
        // given
        EmitterType type1 = EmitterType.builder().id(1L).name("Type1").build();
        EmitterType type2 = EmitterType.builder().id(2L).name("Type2").build();
        List<EmitterType> expected = Arrays.asList(type1, type2);

        when(emitterTypeRepository.findAll()).thenReturn(expected);

        // when
        List<EmitterType> actual = emitterTypeService.getAllEmitterTypes();

        // then
        assertThat(actual).isEqualTo(expected);
        verify(emitterTypeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test get emitter type by id functionality (success)")
    void givenId_getEmitterTypeById_ShouldReturnEmitterTypeWithGivenId() {
        // given
        Long typeId = 1L;
        String typeName = "Type 1";
        EmitterType expected = EmitterType.builder().id(typeId).name(typeName).build();

        when(emitterTypeRepository.findById(typeId)).thenReturn(Optional.of(expected));

        // when
        EmitterType actual = emitterTypeService.getEmitterTypeById(typeId);

        // then
        assertThat(actual.getId()).isEqualTo(typeId);
        assertThat(actual.getName()).isEqualTo(typeName);
        verify(emitterTypeRepository, times(1)).findById(typeId);
    }

    @Test
    @DisplayName("Test get emitter type by id functionality (not found)")
    void givenId_whenGetById_thenThrowServerException() {
        // given
        Long typeId = Long.MAX_VALUE;

        when(emitterTypeRepository.findById(typeId)).thenReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> emitterTypeService.getEmitterTypeById(typeId))
                .isInstanceOf(ServerException.class)
                .hasMessage(String.format("Тип излучателя с идентификатором %d не найден", typeId))
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test get emitter types page functionality (success)")
    void givenPageParameters_whenGetPage_thenReturnEmitterTypesPage() {
        // given
        int pageNumber = 1;
        int pageSize = 5;
        String sortField = "id";
        boolean sortAsc = true;

        Long typeId = 1L;
        String typeName = "Type 1";
        EmitterType emitterType = EmitterType.builder().id(typeId).name(typeName).build();

        PageRequest pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortField).ascending());
        Page<EmitterType> mockPage = new PageImpl<>(List.of(emitterType));

        // when
        given(emitterTypeRepository.findAll(pageable)).willReturn(mockPage);

        Page<EmitterType> emitterTypesPage = emitterTypeService.getEmitterTypesPage(pageNumber, pageSize, sortField,
                sortAsc);

        // then
        assertThat(emitterTypesPage).isNotNull();
        assertThat(emitterTypesPage.getContent().size()).isEqualTo(1);
        assertThat(emitterTypesPage.getContent().get(0).getId()).isEqualTo(typeId);
        assertThat(emitterTypesPage.getContent().get(0).getName()).isEqualTo(typeName);
        verify(emitterTypeRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Test create emitter type functionality (success)")
    void givenEmitterTypeToCreate_whenCreateEmitterType_thenCreatedEmitterTypeIsReturned() {
        // given
        Long typeId = 1L;
        String typeName = "Type 1";
        EmitterType expected = EmitterType.builder().id(typeId).name(typeName).build();

        given(emitterTypeRepository.save(any(EmitterType.class))).willReturn(expected);

        // when
        EmitterType actual = emitterTypeService.createEmitterType(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(typeId);
        assertThat(actual.getName()).isEqualTo(expected.getName());
        verify(emitterTypeRepository, times(1)).save(expected);
    }

    @Test
    @DisplayName("Test create emitter type functionality (conflict)")
    void givenEmitterTypeToCreate_whenCreateEmitterType_thenConflictExceptionIsThrown() {
        // given
        Long typeId = 1L;
        String typeName = "Type 1";
        EmitterType emitterType = EmitterType.builder().id(typeId).name(typeName).build();
        String message = String.format("Тип излучателя с названием \"%s\" уже существует", typeName);

        when(emitterTypeRepository.save(any(EmitterType.class)))
                .thenThrow(new DataIntegrityViolationException(message));

        // when - then
        assertThatThrownBy(() -> emitterTypeService.createEmitterType(emitterType))
                .isInstanceOf(ServerException.class)
                .hasMessage(message)
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Test update emitter type functionality (success)")
    void givenExistingEmitterType_whenUpdateEmitterType_thenUpdatedEmitterTypeReturned() {
        // given
        Long typeId = 1L;
        String existingName = "Type 1";
        String newName = "Updated type";

        EmitterType existingEmitterType = EmitterType.builder().id(typeId).name(existingName).build();
        EmitterType newEmitterType = EmitterType.builder().name(newName).build();
        EmitterType updatedEmitterType = EmitterType.builder().id(typeId).name(newName).build();

        when(emitterTypeRepository.findById(typeId)).thenReturn(Optional.of(existingEmitterType));
        when(emitterTypeRepository.save(any(EmitterType.class))).thenReturn(updatedEmitterType);

        // when
        EmitterType result = emitterTypeService.updateEmitterType(typeId, newEmitterType);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(typeId);
        assertThat(result.getName()).isEqualTo(newName);

        verify(emitterTypeRepository).findById(typeId);
        verify(emitterTypeRepository).save(existingEmitterType);
    }

    @Test
    @DisplayName("Test update emitter type functionality (conflict)")
    void givenExistingEmitterType_whenUpdateEmitterTypeWithDuplicateName_thenThrowConflictException() {
        // given
        Long id = 1L;
        String duplicateName = "Type 1";

        EmitterType existingEmitterType = EmitterType.builder().id(id).name("Type 1").build();
        EmitterType newEmitterType = EmitterType.builder().name(duplicateName).build();

        String message = String.format("Тип излучателя с названием \"%s\" уже существует", duplicateName);

        when(emitterTypeRepository.findById(id)).thenReturn(Optional.of(existingEmitterType));
        when(emitterTypeRepository.save(any(EmitterType.class)))
                .thenThrow(new DataIntegrityViolationException(message));

        // when - then
        assertThatThrownBy(() -> emitterTypeService.updateEmitterType(id, newEmitterType))
                .isInstanceOf(ServerException.class)
                .hasMessage(message)
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(emitterTypeRepository).findById(id);
        verify(emitterTypeRepository).save(existingEmitterType);
    }

    @Test
    @DisplayName("Test delete by id functionality (success)")
    void givenId_whenDeleteById_thenDeleteMethodOfRepositoryIsCalled() {
        // given
        Long typeId = 1L;
        EmitterType existedEmitterType = EmitterType.builder().id(typeId).name("Type 1").build();

        given(emitterTypeRepository.findById(anyLong())).willReturn(Optional.of(existedEmitterType));

        // when
        emitterTypeService.deleteEmitterType(typeId);

        // then
        verify(emitterTypeRepository, times(1)).deleteById(typeId);
    }

    @Test
    @DisplayName("Test delete by id functionality (not found)")
    void givenId_whenDeleteById_thenExceptionIsThrown() {
        // given
        Long typeId = Long.MAX_VALUE;
        String message = String.format("Тип излучателя с идентификатором %d не найден", typeId);

        given(emitterTypeRepository.findById(typeId)).willReturn(Optional.empty());

        // when - then    
        assertThatThrownBy(() -> emitterTypeService.deleteEmitterType(typeId))
                .isInstanceOf(ServerException.class)
                .hasMessage(message)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(emitterTypeRepository, never()).deleteById(typeId);
    }
}
