package ru.klokov.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.CoreMatchers.*;

import ru.klokov.backend.dto.emitterowner.EmitterOwnerRequest;
import ru.klokov.backend.dto.emitterowner.EmitterOwnerResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.service.EmitterOwnerService;

@WebMvcTest(EmitterOwnerController.class)
class EmitterOwnerControllerTest {

    private static final String ENDPOINT = "/api/v1/owners";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmitterOwnerService emitterOwnerService;

    @MockBean
    private ModelMapper mapper;

    private EmitterOwner emitterOwner;

    private EmitterOwnerRequest validEmitterOwnerRequest = new EmitterOwnerRequest("Type 1");
    private EmitterOwnerRequest blankEmitterOwnerRequest = new EmitterOwnerRequest("   ");
    private EmitterOwnerRequest invalidSizeEmitterOwnerRequest = new EmitterOwnerRequest("12");

    private EmitterOwnerResponse emitterOwnerResponse;

    private Instant timestamp = Instant.now();

    private static final int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 5;
    private static final String SORT_FIELD = "id";
    private static final boolean SORT_ASCENDING = true;

    @BeforeEach
    public void setUp() {
        emitterOwner = EmitterOwner.builder().id(1L).name("Owner 1").build();
        emitterOwnerResponse = EmitterOwnerResponse.builder().id(1L).name("Owner 1").build();
    }

    @Test
    @DisplayName("Test get all emitter owners pageable functionality")
    void givenPageParameters_whenGetAll_thenListOfThreeEmitterOwnersIsReturned() throws Exception {
        // given
        EmitterOwner secondEmitterOwner = EmitterOwner.builder().id(2L).name("Owner 2").build();
        EmitterOwner thirdEmitterOwner = EmitterOwner.builder().id(3L).name("Owner 3").build();

        List<EmitterOwner> emitterOwnersList = List.of(emitterOwner, secondEmitterOwner, thirdEmitterOwner);

        Page<EmitterOwner> page = new PageImpl<>(emitterOwnersList);

        given(emitterOwnerService.getOwnersPage(PAGE_NUMBER, PAGE_SIZE, SORT_FIELD, SORT_ASCENDING))
                .willReturn(page);
        given(mapper.map(any(EmitterOwner.class), eq(EmitterOwnerResponse.class)))
                .willReturn(emitterOwnerResponse);

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber", is(PAGE_NUMBER - 1)))
                .andExpect(jsonPath("$.content.length()", is(emitterOwnersList.size())));
    }

    @Test
    @DisplayName("Test get emitter owner by id functionality (success)")
    void givenId_whenGetById_thenSuccessResponse() throws Exception {
        // given
        given(emitterOwnerService.getOwnerById(anyLong())).willReturn(emitterOwner);
        given(mapper.map(emitterOwner, EmitterOwnerResponse.class)).willReturn(emitterOwnerResponse);

        // when
        ResultActions result = mockMvc.perform(
                get(ENDPOINT + "/{id}", emitterOwner.getId()).contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(emitterOwnerResponse.getId()))
                .andExpect(jsonPath("$.name").value(emitterOwnerResponse.getName()));
    }

    @Test
    @DisplayName("Test get emitter owner by id functionality (not found)")
    void givenId_whenGetById_thenNotFoundResponse() throws Exception {
        // given
        given(emitterOwnerService.getOwnerById(anyLong())).willThrow(new ApiException(
                HttpStatus.NOT_FOUND,
                String.format("Владелец излучателя с идентификатором %d не найден", emitterOwner.getId()),
                timestamp));

        // when
        ResultActions result = mockMvc.perform(
                get(ENDPOINT + "/{id}", emitterOwner.getId()).contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message",
                        is(String.format("Владелец излучателя с идентификатором %d не найден",
                                emitterOwner.getId()))));
    }

    @Test
    @DisplayName("Test create emitter owner functionality (success)")
    void givenValidEmitterOwnerRequest_whenCreate_thenSuccessResponse() throws Exception {
        // given
        given(mapper.map(any(EmitterOwnerRequest.class), eq(EmitterOwner.class))).willReturn(emitterOwner);
        given(mapper.map(any(EmitterOwner.class), eq(EmitterOwnerResponse.class)))
                .willReturn(emitterOwnerResponse);

        given(emitterOwnerService.createOwner(any(EmitterOwner.class))).willReturn(emitterOwner);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmitterOwnerRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(emitterOwnerResponse.getId()))
                .andExpect(jsonPath("$.name", is(emitterOwnerResponse.getName())));
    }

    @Test
    @DisplayName("Test create emitter owner functionality (blank input bad request)")
    void givenBlankEmitterOwnerRequest_whenCreate_thenBadRequestResponse() throws Exception {
        // given
        given(emitterOwnerService.createOwner(any(EmitterOwner.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Заполните поле \"владелец\"!",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blankEmitterOwnerRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Заполните поле \"владелец\"!")));
    }

    @Test
    @DisplayName("Test create emitter owner functionality (invalid size input bad request)")
    void givenInvalidSizeEmitterOwnerRequest_whenCreate_thenBadRequestResponse() throws Exception {
        // given
        given(emitterOwnerService.createOwner(any(EmitterOwner.class))).willThrow(new ApiException(
                HttpStatus.BAD_REQUEST,
                "Имя владельца должно состоять минимум из 3, и максимум из 50 символов!",
                timestamp));

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSizeEmitterOwnerRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(
                        "Имя владельца должно состоять минимум из 3, и максимум из 50 символов!")));
    }

    @Test
    @DisplayName("Test edit emitter owner functionality (success)")
    void givenValidEmitterOwnerRequest_whenEdit_thenSuccessResponse() throws Exception {
        // given
        EmitterOwner updatedEmitterOwner = EmitterOwner.builder().id(1L).name("Updated").build();
        EmitterOwnerResponse updatedEmitterOwnerResponse = EmitterOwnerResponse.builder().id(1L).name("Updated")
                .build();

        given(mapper.map(any(EmitterOwnerRequest.class), eq(EmitterOwner.class))).willReturn(emitterOwner);
        given(mapper.map(any(EmitterOwner.class), eq(EmitterOwnerResponse.class)))
                .willReturn(updatedEmitterOwnerResponse);

        given(emitterOwnerService.getOwnerById(anyLong())).willReturn(emitterOwner);
        given(emitterOwnerService.updateOwner(anyLong(), any(EmitterOwner.class)))
                .willReturn(updatedEmitterOwner);

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmitterOwnerRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEmitterOwnerResponse.getId()))
                .andExpect(jsonPath("$.name", is(updatedEmitterOwnerResponse.getName())));
    }

    @Test
    @DisplayName("Test edit emitter owner functionality (blank input bad request)")
    void givenBlankEmitterOwnerRequest_whenEdit_thenBadRequestResponse() throws Exception {
        // given
        given(emitterOwnerService.getOwnerById(anyLong())).willReturn(emitterOwner);
        given(emitterOwnerService.updateOwner(anyLong(), any(EmitterOwner.class)))
                .willThrow(new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Заполните поле \"владелец\"!",
                        timestamp));

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blankEmitterOwnerRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Заполните поле \"владелец\"!")));
    }

    @Test
    @DisplayName("Test edit emitter owner functionality (invalid size input bad request)")
    void givenInvalidSizeEmitterOwnerRequest_whenEdit_thenBadRequestResponse() throws Exception {
        // given
        given(emitterOwnerService.getOwnerById(anyLong())).willReturn(emitterOwner);
        given(emitterOwnerService.updateOwner(anyLong(), any(EmitterOwner.class)))
                .willThrow(new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Имя владельца должно состоять минимум из 3, и максимум из 50 символов!",
                        timestamp));

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSizeEmitterOwnerRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(
                        "Имя владельца должно состоять минимум из 3, и максимум из 50 символов!")));
    }

    @Test
    @DisplayName("Test delete emitter owner functionality (success)")
    void givenId_whenDelete_thenSuccessResponse() throws Exception {
        // given
        doNothing().when(emitterOwnerService).deleteOwner(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", emitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        verify(emitterOwnerService, times(1)).deleteOwner(anyLong());
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(String.format("Владелец с id = %d успешно удален",
                        emitterOwner.getId()))));
    }

    @Test
    @DisplayName("Test delete emitter owner functionality (not found)")
    void givenId_whenDelete_thenNotFoundResponse() throws Exception {
        // given
        doThrow(new ApiException(
                HttpStatus.NOT_FOUND,
                String.format("Владелец излучателя с идентификатором %d не найден", emitterOwner.getId()),
                Instant.now())).when(emitterOwnerService).deleteOwner(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", emitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        verify(emitterOwnerService, times(1)).deleteOwner(anyLong());
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message",
                        is(String.format("Владелец излучателя с идентификатором %d не найден",
                                emitterOwner.getId()))));
    }
}
