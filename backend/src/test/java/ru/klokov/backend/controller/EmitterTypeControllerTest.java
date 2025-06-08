package ru.klokov.backend.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.klokov.backend.dto.emittertype.EmitterTypeRequest;
import ru.klokov.backend.dto.emittertype.EmitterTypeResponse;
import ru.klokov.backend.exception.FormValidationException;
import ru.klokov.backend.exception.ParameterValidationException;
import ru.klokov.backend.exception.ServerException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.service.EmitterTypeService;
import ru.klokov.backend.utils.PageUtils;

@WebMvcTest(EmitterTypeController.class)
public class EmitterTypeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmitterTypeService emitterTypeService;

    @MockitoBean
    private ModelMapper mapper;

    @MockitoBean
    private PageUtils pageUtils;

    @Test
    @DisplayName("GET /api/v1/types/all returns list of emitter types")
    void givenEmitterTypesList_whenGetAllEmitterTypes_thenReturnsListOfResponses() throws Exception {
        // given
        Long typeId1 = 1L;
        Long typeId2 = 2L;
        String typeName1 = "Type 1";
        String typeName2 = "Type 2";

        EmitterType emitterType1 = EmitterType.builder().id(typeId1).name(typeName1).build();
        EmitterType emitterType2 = EmitterType.builder().id(typeId2).name(typeName2).build();
        List<EmitterType> emitterTypes = List.of(emitterType1, emitterType2);

        EmitterTypeResponse response1 = new EmitterTypeResponse(typeId1, typeName1);
        EmitterTypeResponse response2 = new EmitterTypeResponse(typeId2, typeName2);

        given(emitterTypeService.getAllEmitterTypes()).willReturn(emitterTypes);
        given(mapper.map(emitterType1, EmitterTypeResponse.class)).willReturn(response1);
        given(mapper.map(emitterType2, EmitterTypeResponse.class)).willReturn(response2);

        // when - then
        mockMvc.perform(get("/api/v1/types/all")).andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.length()").value(emitterTypes.size()))
                        .andExpect(jsonPath("$[0].id").value(typeId1))
                        .andExpect(jsonPath("$[0].name").value(typeName1))
                        .andExpect(jsonPath("$[1].id").value(typeId2))
                        .andExpect(jsonPath("$[1].name").value(typeName2));

        verify(emitterTypeService, times(1)).getAllEmitterTypes();
        verify(mapper, times(1)).map(emitterType1, EmitterTypeResponse.class);
        verify(mapper, times(1)).map(emitterType2, EmitterTypeResponse.class);
    }

    @Test
    @DisplayName("GET /api/v1/types pageable returns paged emitter type response")
    void givenEmitterTypesPage_whenGetAllEmitterTypesPageable_thenReturnsPagedResponse() throws Exception {
        // given
        Long typeId1 = 1L;
        Long typeId2 = 2L;
        String typeName1 = "Type 1";
        String typeName2 = "Type 2";

        String pageParam = "0";
        String sizeParam = "5";
        String fieldParam = "name";
        String directionParam = "asc";

        int pageNumber = 0;
        int pageSize = 5;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        boolean sortDirectionBoolean = true;

        EmitterType emitterType1 = EmitterType.builder().id(typeId1).name(typeName1).build();
        EmitterType emitterType2 = EmitterType.builder().id(typeId2).name(typeName2).build();
        List<EmitterType> content = List.of(emitterType1, emitterType2);

        Page<EmitterType> page = new PageImpl<>(
                        content,
                        PageRequest.of(pageNumber, pageSize, sortDirection, sortField),
                        5);

        EmitterTypeResponse response1 = new EmitterTypeResponse(typeId1, typeName1);
        EmitterTypeResponse response2 = new EmitterTypeResponse(typeId2, typeName2);

        given(pageUtils.getPageNumber(pageParam)).willReturn(pageNumber);
        given(pageUtils.getPageSize(sizeParam)).willReturn(pageSize);
        given(pageUtils.getPageSortField(fieldParam)).willReturn(sortField);
        given(pageUtils.getPageSortDirection(directionParam)).willReturn(sortDirectionBoolean);

        given(emitterTypeService.getEmitterTypesPage(pageNumber, pageSize, sortField, sortDirectionBoolean))
                        .willReturn(page);
        given(mapper.map(emitterType1, EmitterTypeResponse.class)).willReturn(response1);
        given(mapper.map(emitterType2, EmitterTypeResponse.class)).willReturn(response2);

        // when - then
        mockMvc.perform(get("/api/v1/types").param("page", pageParam).param("size", sizeParam)
                        .param("field", fieldParam).param("direction", directionParam))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.pageNumber").value(pageNumber))
                        .andExpect(jsonPath("$.totalPages").value(page.getTotalPages()))
                        .andExpect(jsonPath("$.content.length()").value(content.size()))
                        .andExpect(jsonPath("$.content[0].id").value(typeId1))
                        .andExpect(jsonPath("$.content[0].name").value(typeName1))
                        .andExpect(jsonPath("$.content[1].id").value(typeId2))
                        .andExpect(jsonPath("$.content[1].name").value(typeName2));

        verify(pageUtils).getPageNumber(pageParam);
        verify(pageUtils).getPageSize(sizeParam);
        verify(pageUtils).getPageSortField(fieldParam);
        verify(pageUtils).getPageSortDirection(directionParam);
        verify(emitterTypeService).getEmitterTypesPage(pageNumber, pageSize, sortField, sortDirectionBoolean);
        verify(mapper).map(emitterType1, EmitterTypeResponse.class);
        verify(mapper).map(emitterType2, EmitterTypeResponse.class);
    }

    @Test
    @DisplayName("GET /api/v1/types pageable without page parameters returns paged emitter type response using default page parameters")
    void givenEmitterTypesPageWithoutPageParameters_whenGetAllEmitterTypesPageable_thenReturnsPagedResponse()
                    throws Exception {
        // given
        Long typeId = 1L;
        String typeName = "Type 1";

        int defaultPageNumber = 0;
        int defaultPageSize = 5;
        String defaultSortField = "id";
        Sort.Direction defaultSortDirection = Sort.Direction.ASC;
        boolean defaultSortDirectionBoolean = true;

        EmitterType emitterType = EmitterType.builder().id(typeId).name(typeName).build();
        List<EmitterType> content = List.of(emitterType);

        Page<EmitterType> page = new PageImpl<>(content,
                        PageRequest.of(defaultPageNumber, defaultPageSize, defaultSortDirection,
                                        defaultSortField),
                        5);

        EmitterTypeResponse response = new EmitterTypeResponse(typeId, typeName);

        given(pageUtils.getPageNumber(null)).willReturn(defaultPageNumber);
        given(pageUtils.getPageSize(null)).willReturn(defaultPageSize);
        given(pageUtils.getPageSortField(null)).willReturn(defaultSortField);
        given(pageUtils.getPageSortDirection(null)).willReturn(defaultSortDirectionBoolean);

        given(emitterTypeService.getEmitterTypesPage(defaultPageNumber, defaultPageSize,
                        defaultSortField,
                        defaultSortDirectionBoolean)).willReturn(page);
        given(mapper.map(emitterType, EmitterTypeResponse.class)).willReturn(response);

        // when - then
        mockMvc.perform(get("/api/v1/types")).andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.pageNumber").value(defaultPageNumber))
                        .andExpect(jsonPath("$.totalPages").value(page.getTotalPages()))
                        .andExpect(jsonPath("$.content.length()").value(1))
                        .andExpect(jsonPath("$.content[0].id").value(typeId))
                        .andExpect(jsonPath("$.content[0].name").value(typeName));

        verify(pageUtils).getPageNumber(null);
        verify(pageUtils).getPageSize(null);
        verify(pageUtils).getPageSortField(null);
        verify(pageUtils).getPageSortDirection(null);
        verify(emitterTypeService).getEmitterTypesPage(
                        defaultPageNumber,
                        defaultPageSize,
                        defaultSortField,
                        defaultSortDirectionBoolean);
        verify(mapper).map(emitterType, EmitterTypeResponse.class);
    }

    @Test
    @DisplayName("GET /api/v1/types pageable with invalid page parameter throws ParameterValidationException")
    void givenEmitterTypesPageWithInvalidPageParameters_whenGetAllEmitterTypesPageable_thenThrowsParameterValidationException()
                    throws Exception {
        // given
        String invalidPageNumber = "invalid";
        String message = "Некорректный параметр \"Номер страницы\"";

        given(pageUtils.getPageNumber(invalidPageNumber))
                        .willThrow(new ParameterValidationException(HttpStatus.BAD_REQUEST, message,
                                        Instant.now()));

        // when - then
        mockMvc.perform(get("/api/v1/types").param("page", invalidPageNumber))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result
                                        .getResolvedException() instanceof ParameterValidationException))
                        .andExpect(result -> assertEquals(message,
                                        result.getResolvedException().getMessage()));

        verify(pageUtils).getPageNumber(invalidPageNumber);
        verifyNoInteractions(emitterTypeService);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("GET /api/v1/types/{id} get emitter type by id functionality (success)")
    void givenId_whenGetEmitterTypeById_thenReturnSuccessResponse() throws Exception {
        // given
        Long typeId = 1L;
        String typeName = "Type 1";

        EmitterType emitterType = EmitterType.builder().id(typeId).name(typeName).build();
        EmitterTypeResponse emitterTypeResponse = new EmitterTypeResponse(typeId, typeName);

        given(emitterTypeService.getEmitterTypeById(anyLong())).willReturn(emitterType);
        given(mapper.map(any(EmitterType.class), eq(EmitterTypeResponse.class)))
                        .willReturn(emitterTypeResponse);

        // when - then
        mockMvc.perform(get("/api/v1/types/{id}", typeId)).andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(typeId))
                        .andExpect(jsonPath("$.name").value(typeName));

        verify(emitterTypeService).getEmitterTypeById(anyLong());
        verify(mapper).map(any(EmitterType.class), eq(EmitterTypeResponse.class));
    }

    @Test
    @DisplayName("GET /api/v1/types/{id} get emitter type by id functionality (not found)")
    void givenId_whenGetEmitterTypeById_thenReturnNotFoundResponse() throws Exception {
        // given
        Long typeId = Long.MAX_VALUE;
        String message = String.format("Тип излучателя с идентификатором %d не найден", typeId);

        given(emitterTypeService.getEmitterTypeById(anyLong()))
                        .willThrow(new ServerException(HttpStatus.NOT_FOUND, message, Instant.now()));

        // when - then
        mockMvc.perform(get("/api/v1/types/{id}", typeId)).andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(
                                        result.getResolvedException() instanceof ServerException))
                        .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                        .andExpect(jsonPath("$.message").value(message))
                        .andExpect(jsonPath("$.timestamp").exists());

        verify(emitterTypeService).getEmitterTypeById(typeId);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("POST /api/v1/types create emitter type functionality (success)")
    void givenValidEmitterTypeRequest_whenCreateEmitterType_thenReturnSuccessResponse()
                    throws Exception {
        // given
        Long typeId = 1L;
        String typeName = "Type 1";

        String requestJson = """
                        {
                            "name": "Type 1"
                        }
                        """;

        EmitterType emitterType = EmitterType.builder().id(typeId).name(typeName).build();
        EmitterTypeResponse emitterTypeResponse = new EmitterTypeResponse(typeId, typeName);

        given(mapper.map(any(EmitterTypeRequest.class), eq(EmitterType.class)))
                        .willReturn(emitterType);
        given(emitterTypeService.createEmitterType(any(EmitterType.class))).willReturn(emitterType);
        given(mapper.map(any(EmitterType.class), eq(EmitterTypeResponse.class)))
                        .willReturn(emitterTypeResponse);

        // when - then
        mockMvc.perform(post("/api/v1/types").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(typeId))
                        .andExpect(jsonPath("$.name").value(typeName));

        verify(mapper).map(any(EmitterTypeRequest.class), eq(EmitterType.class));
        verify(emitterTypeService).createEmitterType(any(EmitterType.class));
        verify(mapper).map(any(EmitterType.class), eq(EmitterTypeResponse.class));
    }

    @Test
    @DisplayName("POST /api/v1/types create emitter type with empty name functionality (conflict)")
    void givenEmitterTypeRequestWithExistingName_whenCreateEmitterType_thenReturnConflictResponse()
                    throws Exception {
        // given
        String duplicateName = "Duplicate name";
        String requestJson = """
                        {
                            "name": "Duplicate name"
                        }
                        """;
        String message = String.format("Тип излучателя с названием \"%s\" уже существует", duplicateName);
        EmitterType emitterType = EmitterType.builder().name(duplicateName).build();

        given(mapper.map(any(EmitterTypeRequest.class), eq(EmitterType.class)))
                        .willReturn(emitterType);
        given(emitterTypeService.createEmitterType(emitterType)).willThrow(new ServerException(
                        HttpStatus.CONFLICT,
                        message,
                        Instant.now()));

        // when - then
        mockMvc.perform(post("/api/v1/types").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isConflict())
                        .andExpect(result -> assertTrue(result
                                        .getResolvedException() instanceof ServerException))
                        .andExpect(jsonPath("$.statusCode").value(HttpStatus.CONFLICT.value()))
                        .andExpect(jsonPath("$.message").value(message))
                        .andExpect(jsonPath("$.timestamp").exists());

        verify(emitterTypeService).createEmitterType(emitterType);
        verify(mapper).map(any(EmitterTypeRequest.class), eq(EmitterType.class));
    }

    @Test
    @DisplayName("POST /api/v1/types create emitter type with empty name functionality (bad request)")
    void givenEmitterTypeRequestWithEmptyName_whenCreateEmitterType_thenReturnBadRequestResponse()
                    throws Exception {
        // given
        String requestJson = """
                        {
                            "name": ""
                        }
                        """;

        // when - then
        mockMvc.perform(post("/api/v1/types").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result
                                        .getResolvedException() instanceof FormValidationException))
                        .andExpect(jsonPath("$.errors.name").isArray())
                        .andExpect(jsonPath("$.errors.name",
                                        hasItem("Заполните поле \"Тип излучателя\"")))
                        .andExpect(jsonPath("$.errors.name", hasItem(
                                        "Тип излучателя должен состоять минимум из 3 символов")))
                        .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(emitterTypeService);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("POST /api/v1/types create emitter type with invalid size name functionality (bad request)")
    void givenEmitterTypeRequestWithInvalidSizeName_whenCreateEmitterType_thenReturnBadRequestResponse()
                    throws Exception {
        // given
        String requestJson = """
                        {
                            "name": "Ty"
                        }
                        """;

        // when - then
        mockMvc.perform(post("/api/v1/types").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result
                                        .getResolvedException() instanceof FormValidationException))
                        .andExpect(jsonPath("$.errors.name").isArray())
                        .andExpect(jsonPath("$.errors.name", hasItem(
                                        "Тип излучателя должен состоять минимум из 3 символов")))
                        .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(emitterTypeService);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("PUT /api/v1/types/{id} update emitter type by id functionality (success)")
    void givenEmitterTypeRequestAndId_whenUpdateEmitterType_thenReturnUpdatedEmitterTypeResponse()
                    throws Exception {
        // given
        Long typeId = 1L;
        String updatedName = "Updated type";

        String requestJson = """
                        {
                            "name": "Updated type"
                        }
                        """;

        EmitterType emitterTypeToUpdate = EmitterType.builder().name(updatedName).build();
        EmitterType updatedEmitterType = EmitterType.builder().id(typeId).name(updatedName).build();
        EmitterTypeResponse emitterTypeResponse = new EmitterTypeResponse(typeId, updatedName);

        given(mapper.map(any(EmitterTypeRequest.class), eq(EmitterType.class)))
                        .willReturn(emitterTypeToUpdate);
        given(emitterTypeService.updateEmitterType(anyLong(), any(EmitterType.class)))
                        .willReturn(updatedEmitterType);
        given(mapper.map(any(EmitterType.class), eq(EmitterTypeResponse.class)))
                        .willReturn(emitterTypeResponse);

        // when - then
        mockMvc.perform(put("/api/v1/types/{id}", typeId).contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(typeId))
                        .andExpect(jsonPath("$.name").value(updatedName));

        verify(mapper).map(any(EmitterTypeRequest.class), eq(EmitterType.class));
        verify(emitterTypeService).updateEmitterType(eq(typeId), any(EmitterType.class));
        verify(mapper).map(any(EmitterType.class), eq(EmitterTypeResponse.class));
    }

    @Test
    @DisplayName("PUT /api/v1/types/{id} update emitter type with empty name by id functionality (bad request)")
    void givenEmitterTypeRequestWithEmptyNameAndId_whenUpdateEmitterType_thenReturnBadRequestResponse()
                    throws Exception {
        // given
        String requestJson = """
                        {
                            "name": ""
                        }
                        """;

        Long typeId = 1L;

        // when - then
        mockMvc.perform(put("/api/v1/types/{id}", typeId).contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result
                                        .getResolvedException() instanceof FormValidationException))
                        .andExpect(jsonPath("$.errors.name").isArray())
                        .andExpect(jsonPath("$.errors.name",
                                        hasItem("Заполните поле \"Тип излучателя\"")))
                        .andExpect(jsonPath("$.errors.name", hasItem(
                                        "Тип излучателя должен состоять минимум из 3 символов")))
                        .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(emitterTypeService);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("PUT /api/v1/types/{id} update emitter type with invalid size name by id functionality (bad request)")
    void givenEmitterTypeRequestWithInvalidSizeNameAndId_whenUpdateEmitterType_thenReturnBadRequestResponse()
                    throws Exception {
        // given
        String requestJson = """
                        {
                            "name": "Ty"
                        }
                        """;

        Long typeId = 1L;

        // when - then
        mockMvc.perform(put("/api/v1/types/{id}", typeId).contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result
                                        .getResolvedException() instanceof FormValidationException))
                        .andExpect(jsonPath("$.errors.name").isArray())
                        .andExpect(jsonPath("$.errors.name", hasItem(
                                        "Тип излучателя должен состоять минимум из 3 символов")))
                        .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(emitterTypeService);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("DELETE /api/v1/types/{id} delete emitter type by id functionality (success)")
    void givenId_whenDeleteEmitterType_thenReturnSuccessResponse() throws Exception {
        // given
        Long typeId = 1L;
        String message = String.format("Тип излучателя с идентификатором %d успешно удален", typeId);

        doNothing().when(emitterTypeService).deleteEmitterType(typeId);

        // when - then
        mockMvc.perform(delete("/api/v1/types/{id}", typeId))
                        .andExpect(status().isOk())
                        .andExpect(content().string(message));

        verify(emitterTypeService).deleteEmitterType(typeId);
    }

    @Test
    @DisplayName("DELETE /api/v1/types/{id} delete emitter type by id functionality (not found)")
    void givenId_whenDeleteEmitterType_thenReturnNotFoundResponse() throws Exception {
        // given
        Long typeId = Long.MAX_VALUE;
        String message = String.format("Тип излучателя с идентификатором %d не найден", typeId);

        doThrow(new ServerException(
                        HttpStatus.NOT_FOUND,
                        message,
                        Instant.now())).when(emitterTypeService).deleteEmitterType(typeId);

        // when - then
        mockMvc.perform(delete("/api/v1/types/{id}", typeId))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                        .andExpect(jsonPath("$.message").value(message))
                        .andExpect(jsonPath("$.timestamp").exists());

        verify(emitterTypeService).deleteEmitterType(typeId);
    }
}
