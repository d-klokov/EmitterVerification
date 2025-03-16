package ru.klokov.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.klokov.backend.dto.emitter.EmitterRequest;
import ru.klokov.backend.dto.emitter.EmitterResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.Emitter;
import ru.klokov.backend.service.EmitterOwnerService;
import ru.klokov.backend.service.EmitterService;
import ru.klokov.backend.service.EmitterTypeService;
import ru.klokov.backend.utils.PageUtils;
import ru.klokov.backend.utils.TestEmitterUtils;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmitterController.class)
class EmitterControllerTest {

    private static final String ENDPOINT = "/api/v1/emitters";

    private final TestEmitterUtils emitterUtils = new TestEmitterUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @Getter
    @Setter
    private EmitterTypeService emitterTypeService;

    @MockBean
    @Getter
    @Setter
    private EmitterOwnerService emitterOwnerService;

    @MockBean
    private EmitterService emitterService;

    @MockBean
    private ModelMapper mapper;

    @MockBean
    private PageUtils pageUtils;

    private final Instant timestamp = Instant.now();

    @Test
    @DisplayName("Test get all emitters pageable functionality")
    void givenPageParameters_whenGetAll_thenListOfTwoEmittersIsReturned() throws Exception {
        // given
        Emitter emitterWithInternalGenerator = emitterUtils.getEmitterWithInternalGenerator();
        Emitter emitterWithoutInternalGenerator = emitterUtils.getEmitterWithoutInternalGenerator();

        List<Emitter> emittersList = List.of(emitterWithInternalGenerator, emitterWithoutInternalGenerator);

        Page<Emitter> page = new PageImpl<>(emittersList);

        given(pageUtils.getPageNumber(any())).willReturn(1);
        given(pageUtils.getPageSize(any())).willReturn(5);
        given(pageUtils.getPageSortField(any())).willReturn("id");
        given(pageUtils.getPageSortDirection(any())).willReturn(true);

        given(emitterService.getEmittersPage(any(Integer.class), any(Integer.class), any(String.class), any(Boolean.class))).willReturn(page);

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber", is(0)))
                .andExpect(jsonPath("$.content.length()", is(emittersList.size())));
    }

    @Test
    @DisplayName("Test get emitter by id functionality (success)")
    void givenId_whenGetById_thenSuccessResponse() throws Exception {
        // given
        Long emitterId = 1L;
        Emitter emitterWithInternalGenerator = emitterUtils.getEmitterWithInternalGenerator();
        EmitterResponse emitterWithInternalGeneratorResponse = emitterUtils.getEmitterResponseWithInternalGenerator();

        given(emitterService.getEmitterById(anyLong())).willReturn(emitterWithInternalGenerator);
        given(mapper.map(emitterWithInternalGenerator, EmitterResponse.class)).willReturn(emitterWithInternalGeneratorResponse);

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT + "/{id}", emitterId).contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(emitterWithInternalGeneratorResponse.getId()))
                .andExpect(jsonPath("$.factoryNumber").value(emitterWithInternalGeneratorResponse.getFactoryNumber()));
    }

    @Test
    @DisplayName("Test get emitter by id functionality (not found)")
    void givenId_whenGetById_thenNotFoundResponse() throws Exception {
        // given
        Long emitterId = 1L;

        given(emitterService.getEmitterById(anyLong())).willThrow(new ApiException(HttpStatus.NOT_FOUND, String.format("Излучатель с идентификатором %d не найден", emitterId), timestamp));

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT + "/{id}", emitterId).contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(String.format("Излучатель с идентификатором %d не найден", emitterId))));
    }

    @Test
    @DisplayName("Test create emitter with internal generator functionality (success)")
    void givenEmitterRequestWithInternalGenerator_whenCreate_thenSuccessResponse() throws Exception {
        // given
        Emitter emitterWithInternalGenerator = emitterUtils.getEmitterWithInternalGenerator();
        EmitterRequest emitterWithInternalGeneratorRequest = emitterUtils.getEmitterRequestWithInternalGenerator();
        EmitterResponse emitterWithInternalGeneratorResponse = emitterUtils.getEmitterResponseWithInternalGenerator();

        given(mapper.map(any(EmitterRequest.class), eq(Emitter.class))).willReturn(emitterWithInternalGenerator);
        given(mapper.map(any(Emitter.class), eq(EmitterResponse.class)))
                .willReturn(emitterWithInternalGeneratorResponse);

        given(emitterService.createEmitter(any(Emitter.class))).willReturn(emitterWithInternalGenerator);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterWithInternalGeneratorRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(emitterWithInternalGeneratorResponse.getId()))
                .andExpect(jsonPath("$.factoryNumber", is(emitterWithInternalGeneratorResponse.getFactoryNumber())));
    }

    @Test
    @DisplayName("Test create emitter without internal generator functionality (success)")
    void givenEmitterRequestWithoutInternalGenerator_whenCreate_thenSuccessResponse() throws Exception {
        // given
        Emitter emitterWithoutInternalGenerator = emitterUtils.getEmitterWithoutInternalGenerator();
        EmitterRequest emitterWithoutInternalGeneratorRequest = emitterUtils.getEmitterRequestWithoutInternalGenerator();
        EmitterResponse emitterWithoutInternalGeneratorResponse = emitterUtils.getEmitterResponseWithoutInternalGenerator();

        given(mapper.map(any(EmitterRequest.class), eq(Emitter.class))).willReturn(emitterWithoutInternalGenerator);
        given(mapper.map(any(Emitter.class), eq(EmitterResponse.class)))
                .willReturn(emitterWithoutInternalGeneratorResponse);

        given(emitterService.createEmitter(any(Emitter.class))).willReturn(emitterWithoutInternalGenerator);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterWithoutInternalGeneratorRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(emitterWithoutInternalGeneratorResponse.getId()))
                .andExpect(jsonPath("$.factoryNumber", is(emitterWithoutInternalGeneratorResponse.getFactoryNumber())));
    }

    @Test
    @DisplayName("Test create emitter with internal generator functionality (constraint exception:  value of minimumPulseFrequency10 is not null")
    void givenEmitterRequestWithInternalGeneratorAndNotNullValueOfMinimumPulseFrequency10_whenCreate_thenBadRequestExceptionIsThrown() throws Exception {
        // given
        Emitter emitterWithInternalGenerator = emitterUtils.getEmitterWithInternalGenerator();
        EmitterRequest emitterWithInternalGeneratorRequest = emitterUtils.getEmitterRequestWithInternalGenerator();

        emitterWithInternalGenerator.setMinimumPulseFrequency10(8.0);
        emitterWithInternalGeneratorRequest.setMinimumPulseFrequency10(8.0);

        given(mapper.map(any(EmitterRequest.class), eq(Emitter.class))).willReturn(emitterWithInternalGenerator);

        given(emitterService.createEmitter(any(Emitter.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Для излучателя с внутренним генератором поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\" должно быть пустым!",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterWithInternalGeneratorRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Для излучателя с внутренним генератором поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\" должно быть пустым!")));
    }

    @Test
    @DisplayName("Test create emitter without internal generator functionality (constraint exception:  value of minimumPulseFrequency10 is null")
    void givenEmitterRequestWithoutInternalGeneratorAndNullValueOfMinimumPulseFrequency10_whenCreate_thenBadRequestExceptionIsThrown() throws Exception {
        // given
        EmitterRequest emitterWithoutInternalGeneratorRequest = emitterUtils.getEmitterRequestWithoutInternalGenerator();
        emitterWithoutInternalGeneratorRequest.setMinimumPulseFrequency10(null);

        Emitter emitterWithoutInternalGenerator = emitterUtils.getEmitterWithoutInternalGenerator();
        emitterWithoutInternalGenerator.setMinimumPulseFrequency10(null);

        given(mapper.map(any(EmitterRequest.class), eq(Emitter.class))).willReturn(emitterWithoutInternalGenerator);

        given(emitterService.createEmitter(any(Emitter.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Заполните поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\"",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterWithoutInternalGeneratorRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Заполните поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\"")));
    }

    @Test
    @DisplayName("Test create emitter functionality (blank input bad request)")
    void givenEmitterRequestWithBlankFactoryNumber_whenCreate_thenBadRequestResponse() throws Exception {
        // given
        EmitterRequest blankFactoryNumberEmitterRequest = emitterUtils.getEmitterRequestWithBlankFactoryNumber();

        given(emitterService.createEmitter(any(Emitter.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Заполните поле \"заводской номер\"!",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blankFactoryNumberEmitterRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Заполните поле \"заводской номер\"!")));
    }

    @Test
    @DisplayName("Test create emitter functionality (invalid size input bad request)")
    void givenEmitterRequestWithInvalidSizeFactoryNumber_whenCreate_thenBadRequestResponse() throws Exception {
        // given
        EmitterRequest invalidSizeFactoryNumberEmitterRequest = emitterUtils.getEmitterRequestWithInvalidSizeFactoryNumber();
        invalidSizeFactoryNumberEmitterRequest.setFactoryNumber("1");

        given(emitterService.createEmitter(any(Emitter.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Заводской номер излучателя должен состоять минимум из 2 и максимум из 255 символов!",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSizeFactoryNumberEmitterRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(
                        "Заводской номер излучателя должен состоять минимум из 2 и максимум из 255 символов!")));
    }

    @Test
    @DisplayName("Test update emitter with internal generator functionality (success)")
    void givenEmitterRequestWithInternalGenerator_whenEdit_thenSuccessResponse() throws Exception {
        // given
        Long emitterToUpdateId = 1L;
        String updatedFactoryNumber = "Updated factory number";

        EmitterRequest emitterRequestWithInternalGenerator = emitterUtils.getEmitterRequestWithInternalGenerator();
        Emitter emitterWithInternalGenerator = emitterUtils.getEmitterWithInternalGenerator();

        Emitter updatedEmitter = emitterUtils.getEmitterWithInternalGenerator();
        updatedEmitter.setFactoryNumber(updatedFactoryNumber);

        EmitterResponse updatedEmitterResponse = emitterUtils.getEmitterResponseWithInternalGenerator();
        updatedEmitterResponse.setFactoryNumber(updatedFactoryNumber);

        given(mapper.map(any(EmitterRequest.class), eq(Emitter.class))).willReturn(emitterWithInternalGenerator);
        given(mapper.map(any(Emitter.class), eq(EmitterResponse.class))).willReturn(updatedEmitterResponse);

        given(emitterService.getEmitterById(anyLong())).willReturn(emitterWithInternalGenerator);
        given(emitterService.updateEmitter(anyLong(), any(Emitter.class))).willReturn(updatedEmitter);

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterToUpdateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterRequestWithInternalGenerator)));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEmitterResponse.getId()))
                .andExpect(jsonPath("$.factoryNumber", is(updatedEmitterResponse.getFactoryNumber())));
    }

    @Test
    @DisplayName("Test update emitter without internal generator functionality (success)")
    void givenEmitterRequestWithoutInternalGenerator_whenEdit_thenSuccessResponse() throws Exception {
        // given
        Long emitterToUpdateId = 1L;
        String updatedFactoryNumber = "Updated factory number";

        EmitterRequest emitterRequestWithoutInternalGenerator = emitterUtils.getEmitterRequestWithoutInternalGenerator();

        Emitter emitterWithoutInternalGenerator = emitterUtils.getEmitterWithoutInternalGenerator();

        Emitter updatedEmitter = emitterUtils.getEmitterWithoutInternalGenerator();
        updatedEmitter.setFactoryNumber(updatedFactoryNumber);

        EmitterResponse updatedEmitterResponse = emitterUtils.getEmitterResponseWithoutInternalGenerator();
        updatedEmitterResponse.setFactoryNumber(updatedFactoryNumber);

        given(mapper.map(any(EmitterRequest.class), eq(Emitter.class))).willReturn(emitterWithoutInternalGenerator);
        given(mapper.map(any(Emitter.class), eq(EmitterResponse.class))).willReturn(updatedEmitterResponse);

        given(emitterService.getEmitterById(anyLong())).willReturn(emitterWithoutInternalGenerator);
        given(emitterService.updateEmitter(anyLong(), any(Emitter.class))).willReturn(updatedEmitter);

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterToUpdateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterRequestWithoutInternalGenerator)));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEmitterResponse.getId()))
                .andExpect(jsonPath("$.factoryNumber", is(updatedEmitterResponse.getFactoryNumber())));
    }

    @Test
    @DisplayName("Test update emitter with internal generator functionality (constraint exception:  value of minimumPulseFrequency10 is not null)")
    void givenEmitterRequestWithInternalGenerator_whenEdit_thenBadRequestResponse() throws Exception {
        // given
        String updatedFactoryNumber = "Updated factory number";

        Emitter emitterWithInternalGenerator = emitterUtils.getEmitterWithInternalGenerator();
        EmitterRequest emitterRequestWithInternalGenerator = emitterUtils.getEmitterRequestWithInternalGenerator();
        emitterRequestWithInternalGenerator.setFactoryNumber(updatedFactoryNumber);
        emitterRequestWithInternalGenerator.setMinimumPulseFrequency10(8.0);

        given(mapper.map(any(EmitterRequest.class), eq(Emitter.class))).willReturn(emitterWithInternalGenerator);

        given(emitterService.createEmitter(any(Emitter.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Для излучателя с внутренним генератором поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\" должно быть пустым!",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterRequestWithInternalGenerator)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Для излучателя с внутренним генератором поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\" должно быть пустым!")));
    }

    @Test
    @DisplayName("Test update emitter without internal generator functionality (constraint exception:  value of minimumPulseFrequency10 is not null)")
    void givenEmitterRequestWithoutInternalGenerator_whenEdit_thenBadRequestResponse() throws Exception {
        // given
        String updatedFactoryNumber = "Updated factory number";

        Emitter emitterWithoutInternalGenerator = emitterUtils.getEmitterWithoutInternalGenerator();
        EmitterRequest emitterRequestWithoutInternalGenerator = emitterUtils.getEmitterRequestWithoutInternalGenerator();
        emitterRequestWithoutInternalGenerator.setFactoryNumber(updatedFactoryNumber);
        emitterRequestWithoutInternalGenerator.setMinimumPulseFrequency10(8.0);

        given(mapper.map(any(EmitterRequest.class), eq(Emitter.class))).willReturn(emitterWithoutInternalGenerator);

        given(emitterService.createEmitter(any(Emitter.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Заполните поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\"",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterRequestWithoutInternalGenerator)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Заполните поле \"Минимальная допутимая частота внешнего генератора (10 Гц)\"")));
    }

    @Test
    @DisplayName("Test edit emitter functionality (blank input bad request)")
    void givenBlankFactoryNumberEmitterRequest_whenEdit_thenBadRequestResponse() throws Exception {
        // given
        Long emitterToUpdateId = 1L;

        EmitterRequest emitterRequestWithBlankFactoryNumber = emitterUtils.getEmitterRequestWithBlankFactoryNumber();

        Emitter emitterWithInternalGenerator = emitterUtils.getEmitterWithInternalGenerator();

        given(mapper.map(emitterRequestWithBlankFactoryNumber, Emitter.class)).willReturn(emitterWithInternalGenerator);

        given(emitterService.updateEmitter(anyLong(), any(Emitter.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Заполните поле \"заводской номер\"!",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterToUpdateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterRequestWithBlankFactoryNumber)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Заполните поле \"заводской номер\"!")));
    }

    @Test
    @DisplayName("Test edit emitter functionality (invalid size input bad request)")
    void givenInvalidSizeEmitterRequest_whenEdit_thenBadRequestResponse() throws Exception {
        // given
        Long emitterToUpdateId = 1L;

        EmitterRequest emitterRequestWithInvalidSizeFactoryNumber = emitterUtils.getEmitterRequestWithInvalidSizeFactoryNumber();

        Emitter emitterWithInternalGenerator = emitterUtils.getEmitterWithInternalGenerator();

        given(mapper.map(emitterRequestWithInvalidSizeFactoryNumber, Emitter.class)).willReturn(emitterWithInternalGenerator);

        given(emitterService.getEmitterById(anyLong())).willReturn(emitterWithInternalGenerator);
        given(emitterService.updateEmitter(anyLong(), any(Emitter.class)))
                .willThrow(new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Заводской номер излучателя должен состоять минимум из 2 и максимум из 255 символов!",
                        timestamp));

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterToUpdateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterRequestWithInvalidSizeFactoryNumber)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(
                        "Заводской номер излучателя должен состоять минимум из 2 и максимум из 255 символов!")));
    }

    @Test
    @DisplayName("Test delete emitter by id functionality (success)")
    void givenEmitterId_whenDelete_thenSuccessResponse() throws Exception {
        // given
        Long emitterToDeleteId = 1L;

        doNothing().when(emitterService).deleteEmitter(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", emitterToDeleteId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        verify(emitterService, times(1)).deleteEmitter(emitterToDeleteId);
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(String.format("Излучатель с id = %d успешно удален",
                        emitterToDeleteId))));
    }

    @Test
    @DisplayName("Test delete emitter by id functionality (not found)")
    void givenEmitterId_whenDelete_thenNotFoundResponse() throws Exception {
        // given
        Long emitterToDeleteId = 1L;

        doThrow(new ApiException(
                HttpStatus.NOT_FOUND,
                String.format("Излучатель с идентификатором %d не найден", emitterToDeleteId),
                Instant.now())).when(emitterService).deleteEmitter(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", emitterToDeleteId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        verify(emitterService, times(1)).deleteEmitter(anyLong());
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message",
                        is(String.format("Излучатель с идентификатором %d не найден", emitterToDeleteId))));
    }
}