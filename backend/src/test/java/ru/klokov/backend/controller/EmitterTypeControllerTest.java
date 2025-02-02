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

import ru.klokov.backend.dto.emittertype.EmitterTypeRequest;
import ru.klokov.backend.dto.emittertype.EmitterTypeResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.service.EmitterTypeService;

@WebMvcTest(EmitterTypeController.class)
class EmitterTypeControllerTest {

        private static final String ENDPOINT = "/api/v1/types";

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private EmitterTypeService emitterTypeService;

        @MockBean
        private ModelMapper mapper;

        private EmitterType emitterType;

        private EmitterTypeRequest validEmitterTypeRequest = new EmitterTypeRequest("Type 1");
        private EmitterTypeRequest blankEmitterTypeRequest = new EmitterTypeRequest("   ");
        private EmitterTypeRequest invalidSizeEmitterTypeRequest = new EmitterTypeRequest("12");

        private EmitterTypeResponse emitterTypeResponse;

        private Instant timestamp = Instant.now();

        private static final int PAGE_NUMBER = 1;
        private static final int PAGE_SIZE = 5;
        private static final String SORT_FIELD = "id";
        private static final boolean SORT_ASCENDING = true;

        @BeforeEach
        public void setUp() {
                emitterType = EmitterType.builder().id(1L).name("Type 1").build();
                emitterTypeResponse = EmitterTypeResponse.builder().id(1L).name("Type 1").build();
        }

        @Test
        @DisplayName("Test get all emitter types pageable functionality")
        void givenPageParameters_whenGetAll_thenListOfThreeEmitterTypesIsReturned() throws Exception {
                // given
                EmitterType secondEmitterType = EmitterType.builder().id(2L).name("Type 2").build();
                EmitterType thirdEmitterType = EmitterType.builder().id(3L).name("Type 3").build();

                List<EmitterType> emitterTypesList = List.of(emitterType, secondEmitterType, thirdEmitterType);

                Page<EmitterType> page = new PageImpl<>(emitterTypesList);

                given(emitterTypeService.getEmitterTypesPage(PAGE_NUMBER, PAGE_SIZE, SORT_FIELD, SORT_ASCENDING))
                                .willReturn(page);
                given(mapper.map(any(EmitterType.class), eq(EmitterTypeResponse.class)))
                                .willReturn(emitterTypeResponse);

                // when
                ResultActions result = mockMvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON));

                // then
                result
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.pageNumber", is(PAGE_NUMBER - 1)))
                                .andExpect(jsonPath("$.content.length()", is(emitterTypesList.size())));
        }

        @Test
        @DisplayName("Test get emitter type by id functionality (success)")
        void givenId_whenGetById_thenSuccessResponse() throws Exception {
                // given
                given(emitterTypeService.getEmitterTypeById(anyLong())).willReturn(emitterType);
                given(mapper.map(emitterType, EmitterTypeResponse.class)).willReturn(emitterTypeResponse);

                // when
                ResultActions result = mockMvc.perform(
                                get(ENDPOINT + "/{id}", emitterType.getId()).contentType(MediaType.APPLICATION_JSON));

                // then
                result
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(emitterTypeResponse.getId()))
                                .andExpect(jsonPath("$.name").value(emitterTypeResponse.getName()));
        }

        @Test
        @DisplayName("Test get emitter type by id functionality (not found)")
        void givenId_whenGetById_thenNotFoundResponse() throws Exception {
                // given
                given(emitterTypeService.getEmitterTypeById(anyLong())).willThrow(new ApiException(
                                HttpStatus.NOT_FOUND,
                                String.format("Тип излучателя с идентификатором %d не найден", emitterType.getId()),
                                timestamp));

                // when
                ResultActions result = mockMvc.perform(
                                get(ENDPOINT + "/{id}", emitterType.getId()).contentType(MediaType.APPLICATION_JSON));

                // then
                result
                                .andDo(print())
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                                .andExpect(jsonPath("$.message",
                                                is(String.format("Тип излучателя с идентификатором %d не найден",
                                                                emitterType.getId()))));
        }

        @Test
        @DisplayName("Test create emitter type functionality (success)")
        void givenValidEmitterTypeRequest_whenCreate_thenSuccessResponse() throws Exception {
                // given
                given(mapper.map(any(EmitterTypeRequest.class), eq(EmitterType.class))).willReturn(emitterType);
                given(mapper.map(any(EmitterType.class), eq(EmitterTypeResponse.class)))
                                .willReturn(emitterTypeResponse);

                given(emitterTypeService.createEmitterType(any(EmitterType.class))).willReturn(emitterType);

                // when
                ResultActions result = mockMvc.perform(post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validEmitterTypeRequest)));

                // then
                result
                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(emitterTypeResponse.getId()))
                                .andExpect(jsonPath("$.name", is(emitterTypeResponse.getName())));
        }

        @Test
        @DisplayName("Test create emitter type functionality (blank input bad request)")
        void givenBlankEmitterTypeRequest_whenCreate_thenBadRequestResponse() throws Exception {
                // given
                given(emitterTypeService.createEmitterType(any(EmitterType.class))).willThrow(new ApiException(
                                HttpStatus.BAD_REQUEST,
                                "Заполните поле \"тип излучателя\"!",
                                timestamp));

                // when
                ResultActions result = mockMvc.perform(post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankEmitterTypeRequest)));

                // then
                result
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                                .andExpect(jsonPath("$.message", is("Заполните поле \"тип излучателя\"!")));
        }

        @Test
        @DisplayName("Test create emitter type functionality (invalid size input bad request)")
        void givenInvalidSizeEmitterTypeRequest_whenCreate_thenBadRequestResponse() throws Exception {
                // given
                given(emitterTypeService.createEmitterType(any(EmitterType.class))).willThrow(new ApiException(
                                HttpStatus.BAD_REQUEST,
                                "Тип излучателя должен состоять минимум из 3, и максимум из 50 символов!",
                                timestamp));

                // when
                ResultActions result = mockMvc.perform(post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidSizeEmitterTypeRequest)));

                // then
                result
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                                .andExpect(jsonPath("$.message", is(
                                                "Тип излучателя должен состоять минимум из 3, и максимум из 50 символов!")));
        }

        @Test
        @DisplayName("Test edit emitter type functionality (success)")
        void givenValidEmitterTypeRequest_whenEdit_thenSuccessResponse() throws Exception {
                // given
                EmitterType updatedEmitterType = EmitterType.builder().id(1L).name("Updated").build();
                EmitterTypeResponse updatedEmitterTypeResponse = EmitterTypeResponse.builder().id(1L).name("Updated")
                                .build();

                given(mapper.map(any(EmitterTypeRequest.class), eq(EmitterType.class))).willReturn(emitterType);
                given(mapper.map(any(EmitterType.class), eq(EmitterTypeResponse.class)))
                                .willReturn(updatedEmitterTypeResponse);

                given(emitterTypeService.getEmitterTypeById(anyLong())).willReturn(emitterType);
                given(emitterTypeService.updateEmitterType(anyLong(), any(EmitterType.class)))
                                .willReturn(updatedEmitterType);

                // when
                ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterType.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validEmitterTypeRequest)));

                // then
                result
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(updatedEmitterTypeResponse.getId()))
                                .andExpect(jsonPath("$.name", is(updatedEmitterTypeResponse.getName())));
        }

        @Test
        @DisplayName("Test edit emitter type functionality (blank input bad request)")
        void givenBlankEmitterTypeRequest_whenEdit_thenBadRequestResponse() throws Exception {
                // given
                given(emitterTypeService.updateEmitterType(anyLong(), any(EmitterType.class)))
                                .willThrow(new ApiException(
                                                HttpStatus.BAD_REQUEST,
                                                "Заполните поле \"тип излучателя\"!",
                                                timestamp));

                // when
                ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterType.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankEmitterTypeRequest)));

                // then
                result
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                                .andExpect(jsonPath("$.message", is("Заполните поле \"тип излучателя\"!")));
        }

        @Test
        @DisplayName("Test edit emitter type functionality (invalid size input bad request)")
        void givenInvalidSizeEmitterTypeRequest_whenEdit_thenBadRequestResponse() throws Exception {
                // given
                given(emitterTypeService.updateEmitterType(anyLong(), any(EmitterType.class)))
                                .willThrow(new ApiException(
                                                HttpStatus.BAD_REQUEST,
                                                "Тип излучателя должен состоять минимум из 3, и максимум из 50 символов!",
                                                timestamp));

                // when
                ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", emitterType.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidSizeEmitterTypeRequest)));

                // then
                result
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                                .andExpect(jsonPath("$.message", is(
                                                "Тип излучателя должен состоять минимум из 3, и максимум из 50 символов!")));
        }

        @Test
        @DisplayName("Test delete emitter type functionality (success)")
        void givenId_whenDelete_thenSuccessResponse() throws Exception {
                // given
                doNothing().when(emitterTypeService).deleteEmitterType(anyLong());

                // when
                ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", emitterType.getId())
                                .contentType(MediaType.APPLICATION_JSON));

                // then
                verify(emitterTypeService, times(1)).deleteEmitterType(anyLong());
                result
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", is(String.format("Тип излучателя с id = %d успешно удален",
                                                emitterType.getId()))));
        }

        @Test
        @DisplayName("Test delete emitter type functionality (not found)")
        void givenId_whenDelete_thenNotFoundResponse() throws Exception {
                // given
                doThrow(new ApiException(
                                HttpStatus.NOT_FOUND,
                                String.format("Тип излучателя с идентификатором %d не найден", emitterType.getId()),
                                Instant.now())).when(emitterTypeService).deleteEmitterType(anyLong());

                // when
                ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", emitterType.getId())
                                .contentType(MediaType.APPLICATION_JSON));

                // then
                verify(emitterTypeService, times(1)).deleteEmitterType(anyLong());
                result
                                .andDo(print())
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                                .andExpect(jsonPath("$.message",
                                                is(String.format("Тип излучателя с идентификатором %d не найден",
                                                                emitterType.getId()))));
        }
}
