package ru.klokov.backend.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.klokov.backend.dto.emittertype.EmitterTypeRequest;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.repository.EmitterTypeRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmitterTypeRestControllerV1IntegrationTest extends AbstractRestControllerBaseTest {

    private static final String ENDPOINT = "/api/v1/types";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmitterTypeRepository emitterTypeRepository;

    @BeforeAll
    static void beforeAll() {
        POSTGRE_SQL_CONTAINER.start();
    }

    @AfterAll
    static void afterAll() {
        POSTGRE_SQL_CONTAINER.stop();
    }

    @BeforeEach
    public void setUp() {
        emitterTypeRepository.deleteAll();
    }

    @Test
    @DisplayName("Test get all emitter types pageable functionality")
    void givenPageParameters_whenGetAll_thenListOfThreeEmitterTypesIsReturned() throws Exception {
        // given
        EmitterType firstEmitterType = EmitterType.builder().name("Type 1").build();
        EmitterType secondEmitterType = EmitterType.builder().name("Type 2").build();
        EmitterType thirdEmitterType = EmitterType.builder().name("Type 3").build();

        List<EmitterType> emitterTypesList = List.of(firstEmitterType, secondEmitterType, thirdEmitterType);
        emitterTypeRepository.saveAll(emitterTypesList);

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT).contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber", is(0)))
                .andExpect(jsonPath("$.content.length()", is(emitterTypesList.size())));
    }

    @Test
    @DisplayName("Test get emitter type by id functionality (success)")
    void givenId_whenGetById_thenSuccessResponse() throws Exception {
        // given
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();

        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);

        // when
        ResultActions result = mockMvc.perform(
                get(ENDPOINT + "/{id}", savedEmitterType.getId()).contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEmitterType.getId()))
                .andExpect(jsonPath("$.name", is(savedEmitterType.getName())));
    }

    @Test
    @DisplayName("Test get emitter type by id functionality (not found)")
    void givenId_whenGetById_thenNotFoundResponse() throws Exception {
        // given
        EmitterType emitterType = EmitterType.builder().id(1L).name("Type 1").build();

        // when
        ResultActions result = mockMvc.perform(
                get(ENDPOINT + "/{id}", emitterType.getId()).contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message",
                        is(String.format("Тип излучателя с идентификатором %d не найден", emitterType.getId()))));
    }

    @Test
    @DisplayName("Test create emitter type functionality (success)")
    void givenValidEmitterTypeRequest_whenCreate_thenSuccessResponse() throws Exception {
        // given
        EmitterTypeRequest emitterTypeRequest = EmitterTypeRequest.builder().name("Type 1").build();

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterTypeRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(emitterTypeRequest.getName())));
    }

    @Test
    @DisplayName("Test create emitter type functionality (blank input bad request)")
    void givenBlankEmitterTypeRequest_whenCreate_thenBadRequestResponse() throws Exception {
        // given
        EmitterTypeRequest emitterTypeRequest = EmitterTypeRequest.builder().name("   ").build();

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterTypeRequest)));

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
        EmitterTypeRequest emitterTypeRequest = EmitterTypeRequest.builder().name("12").build();

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterTypeRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message",
                        is("Тип излучателя должен состоять минимум из 3, и максимум из 50 символов!")));
    }

    @Test
    @DisplayName("Test edit emitter type functionality (success)")
    void givenValidEmitterTypeRequest_whenEdit_thenSuccessResponse() throws Exception {
        // given
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();
        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);
        String updatedName = "Updated type";
        EmitterTypeRequest emitterTypeRequest = EmitterTypeRequest.builder().name(updatedName).build();
        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", savedEmitterType.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterTypeRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEmitterType.getId()))
                .andExpect(jsonPath("$.name", is(updatedName)));
    }

    @Test
    @DisplayName("Test edit emitter type functionality (blank input bad request)")
    void givenBlankEmitterTypeRequest_whenEdit_thenBadRequestResponse() throws Exception {
        // given
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();
        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);
        String updatedName = "   ";
        EmitterTypeRequest emitterTypeRequest = EmitterTypeRequest.builder().name(updatedName).build();

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", savedEmitterType.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterTypeRequest)));

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
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();
        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);
        String updatedName = "12";
        EmitterTypeRequest emitterTypeRequest = EmitterTypeRequest.builder().name(updatedName).build();

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", savedEmitterType.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterTypeRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message",
                        is("Тип излучателя должен состоять минимум из 3, и максимум из 50 символов!")));
    }

    @Test
    @DisplayName("Test delete emitter type functionality (success)")
    void givenId_whenDelete_thenSuccessResponse() throws Exception {
        // given
        EmitterType emitterType = EmitterType.builder().name("Type 1").build();
        EmitterType savedEmitterType = emitterTypeRepository.save(emitterType);

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", savedEmitterType.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        Optional<EmitterType> obtainedEmitterType = emitterTypeRepository.findById(savedEmitterType.getId());
        Assertions.assertTrue(obtainedEmitterType.isEmpty());

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is(String.format("Тип излучателя с id = %d успешно удален", emitterType.getId()))));
    }

    @Test
    @DisplayName("Test delete emitter type functionality (not found)")
    void givenId_whenDelete_thenNotFoundResponse() throws Exception {
        // given
        Long notExistedEmitterTypeId = 1L;

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", notExistedEmitterTypeId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message",
                        is(String.format("Тип излучателя с идентификатором %d не найден", notExistedEmitterTypeId))));
    }
}