package ru.klokov.backend.it;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.klokov.backend.dto.emitterowner.EmitterOwnerRequest;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.repository.EmitterOwnerRepository;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmitterOwnerRestControllerV1IntegrationTest extends AbstractRestControllerBaseTest {

    private static final String ENDPOINT = "/api/v1/owners";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmitterOwnerRepository emitterOwnerRepository;

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
        emitterOwnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test get all emitter owners pageable functionality")
    void givenPageParameters_whenGetAll_thenListOfThreeEmitterOwnersIsReturned() throws Exception {
        // given
        EmitterOwner firstEmitterOwner = EmitterOwner.builder().name("Owner 1").build();
        EmitterOwner secondEmitterOwner = EmitterOwner.builder().name("Owner 2").build();
        EmitterOwner thirdEmitterOwner = EmitterOwner.builder().name("Owner 3").build();

        List<EmitterOwner> emitterOwnersList = List.of(firstEmitterOwner, secondEmitterOwner, thirdEmitterOwner);
        emitterOwnerRepository.saveAll(emitterOwnersList);

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
        EmitterOwner emitterOwner = EmitterOwner.builder().name("Owner 1").build();

        EmitterOwner savedEmitterOwner = emitterOwnerRepository.save(emitterOwner);

        // when
        ResultActions result = mockMvc.perform(
                get(ENDPOINT + "/{id}", savedEmitterOwner.getId()).contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEmitterOwner.getId()))
                .andExpect(jsonPath("$.name", is(savedEmitterOwner.getName())));
    }

    @Test
    @DisplayName("Test get emitter owner by id functionality (not found)")
    void givenId_whenGetById_thenNotFoundResponse() throws Exception {
        // given
        EmitterOwner emitterOwner = EmitterOwner.builder().id(1L).name("Owner 1").build();

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
        EmitterOwnerRequest emitterOwnerRequest = EmitterOwnerRequest.builder().name("Owner 1").build();

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterOwnerRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(emitterOwnerRequest.getName())));
    }

    @Test
    @DisplayName("Test create emitter owner functionality (blank input bad request)")
    void givenBlankEmitterOwnerRequest_whenCreate_thenBadRequestResponse() throws Exception {
        // given
        EmitterOwnerRequest emitterOwnerRequest = EmitterOwnerRequest.builder().name("   ").build();

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterOwnerRequest)));

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
        EmitterOwnerRequest emitterOwnerRequest = EmitterOwnerRequest.builder().name("12").build();

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterOwnerRequest)));

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
        EmitterOwner emitterOwner = EmitterOwner.builder().name("Owner 1").build();
        EmitterOwner savedEmitterOwner = emitterOwnerRepository.save(emitterOwner);
        String updatedName = "Updated owner";
        EmitterOwnerRequest emitterOwnerRequest = EmitterOwnerRequest.builder().name(updatedName).build();
        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", savedEmitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterOwnerRequest)));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEmitterOwner.getId()))
                .andExpect(jsonPath("$.name", is(updatedName)));
    }

    @Test
    @DisplayName("Test edit emitter owner functionality (blank input bad request)")
    void givenBlankEmitterOwnerRequest_whenEdit_thenBadRequestResponse() throws Exception {
        // given
        EmitterOwner emitterOwner = EmitterOwner.builder().name("Type 1").build();
        EmitterOwner savedEmitterType = emitterOwnerRepository.save(emitterOwner);
        String updatedName = "   ";
        EmitterOwnerRequest emitterOwnerRequest = EmitterOwnerRequest.builder().name(updatedName).build();

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", savedEmitterType.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterOwnerRequest)));

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
        EmitterOwner emitterOwner = EmitterOwner.builder().name("Owner 1").build();
        EmitterOwner savedEmitterOwner = emitterOwnerRepository.save(emitterOwner);
        String updatedName = "12";
        EmitterOwnerRequest emitterOwnerRequest = EmitterOwnerRequest.builder().name(updatedName).build();

        // when
        ResultActions result = mockMvc.perform(put(ENDPOINT + "/{id}", savedEmitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emitterOwnerRequest)));

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
        EmitterOwner emitterOwner = EmitterOwner.builder().name("Owner 1").build();
        EmitterOwner savedEmitterOwner = emitterOwnerRepository.save(emitterOwner);

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", savedEmitterOwner.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        Optional<EmitterOwner> obtainedEmitterOwner = emitterOwnerRepository.findById(savedEmitterOwner.getId());
        assertTrue(obtainedEmitterOwner.isEmpty());

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
        Long notExistedEmitterOwnerId = 1L;

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT + "/{id}", notExistedEmitterOwnerId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message",
                        is(String.format("Владелец излучателя с идентификатором %d не найден",
                                notExistedEmitterOwnerId))));
    }

}
