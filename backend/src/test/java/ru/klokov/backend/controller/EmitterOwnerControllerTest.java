package ru.klokov.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.klokov.backend.dto.emitterowner.EmitterOwnerRequest;
import ru.klokov.backend.dto.emitterowner.EmitterOwnerResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.service.EmitterOwnerService;
import ru.klokov.backend.utils.PageUtils;

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

    @MockBean
    private PageUtils pageUtils;

    private final EmitterOwner emitterOwner = EmitterOwner.builder().id(1L).name("Owner 1").build();

    private final EmitterOwnerRequest validEmitterOwnerRequest = new EmitterOwnerRequest("Type 1");
    private final EmitterOwnerRequest blankEmitterOwnerRequest = new EmitterOwnerRequest("   ");
    private final EmitterOwnerRequest invalidSizeEmitterOwnerRequest = new EmitterOwnerRequest("12");

    private final EmitterOwnerResponse emitterOwnerResponse = EmitterOwnerResponse.builder().id(1L).name("Owner 1").build();

    private final Instant timestamp = Instant.now();

    @Test
    @DisplayName("Test get all emitter owners pageable functionality")
    void givenPageParameters_whenGetAll_thenListOfThreeEmitterOwnersIsReturned() throws Exception {
        // given
        EmitterOwner secondEmitterOwner = EmitterOwner.builder().id(2L).name("Owner 2").build();
        EmitterOwner thirdEmitterOwner = EmitterOwner.builder().id(3L).name("Owner 3").build();

        List<EmitterOwner> emitterOwnersList = List.of(emitterOwner, secondEmitterOwner, thirdEmitterOwner);

        Page<EmitterOwner> page = new PageImpl<>(emitterOwnersList);

        given(pageUtils.getPageNumber(any())).willReturn(1);
        given(pageUtils.getPageSize(any())).willReturn(5);
        given(pageUtils.getPageSortField(any())).willReturn("id");
        given(pageUtils.getPageSortDirection(any())).willReturn(true);

        given(emitterOwnerService.getEmitterOwnersPage(
                any(Integer.class),
                any(Integer.class),
                any(String.class),
                any(Boolean.class))
        ).willReturn(page);

        given(mapper.map(any(EmitterOwner.class), eq(EmitterOwnerResponse.class)))
                .willReturn(emitterOwnerResponse);

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber", is(0)))
                .andExpect(jsonPath("$.content.length()", is(emitterOwnersList.size())));
    }

    @Test
    @DisplayName("Test get emitter owner by id functionality (success)")
    void givenId_whenGetById_thenSuccessResponse() throws Exception {
        // given
        given(emitterOwnerService.getEmitterOwnerById(anyLong())).willReturn(emitterOwner);
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
        given(emitterOwnerService.getEmitterOwnerById(anyLong())).willThrow(new ApiException(
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

        given(emitterOwnerService.createEmitterOwner(any(EmitterOwner.class))).willReturn(emitterOwner);

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
        given(emitterOwnerService.createEmitterOwner(any(EmitterOwner.class))).willThrow(new ApiException(
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
        given(emitterOwnerService.createEmitterOwner(any(EmitterOwner.class))).willThrow(new ApiException(
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

        given(emitterOwnerService.getEmitterOwnerById(anyLong())).willReturn(emitterOwner);
        given(emitterOwnerService.updateEmitterOwner(anyLong(), any(EmitterOwner.class)))
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
        given(emitterOwnerService.getEmitterOwnerById(anyLong())).willReturn(emitterOwner);
        given(emitterOwnerService.updateEmitterOwner(anyLong(), any(EmitterOwner.class)))
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
        given(emitterOwnerService.getEmitterOwnerById(anyLong())).willReturn(emitterOwner);
        given(emitterOwnerService.updateEmitterOwner(anyLong(), any(EmitterOwner.class)))
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
        doNothing().when(emitterOwnerService).deleteEmitterOwner(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", emitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        verify(emitterOwnerService, times(1)).deleteEmitterOwner(anyLong());
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
                Instant.now())).when(emitterOwnerService).deleteEmitterOwner(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", emitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        verify(emitterOwnerService, times(1)).deleteEmitterOwner(anyLong());
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message",
                        is(String.format("Владелец излучателя с идентификатором %d не найден",
                                emitterOwner.getId()))));
    }
}
