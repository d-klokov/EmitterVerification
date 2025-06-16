package ru.klokov.backend.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.klokov.backend.dto.PagedResponse;
import ru.klokov.backend.dto.emittertype.EmitterTypeRequest;
import ru.klokov.backend.dto.emittertype.EmitterTypeResponse;
import ru.klokov.backend.dto.error.FormValidationErrorResponse;
import ru.klokov.backend.dto.error.ParameterValidationErrorResponse;
import ru.klokov.backend.dto.error.ServerErrorResponse;
import ru.klokov.backend.exception.FormValidationException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.service.EmitterTypeService;
import ru.klokov.backend.utils.PageUtils;
import ru.klokov.backend.utils.ValidationUtils;

@RestController
@RequestMapping("/api/v1/types")
@RequiredArgsConstructor
public class EmitterTypeController {

    private final EmitterTypeService emitterTypeService;
    private final ModelMapper mapper;
    private final PageUtils pageUtils;

    @Operation(summary = "Получить список всех типов излучателей",
                    description = "Возвращает список всех типов излучателей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список тип излучателей получен",
                            content = {@Content(mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(
                                                            implementation = EmitterTypeResponse.class)))})
    })
    @GetMapping("/all")
    public ResponseEntity<List<EmitterTypeResponse>> getAllEmitterTypes() {
        return ResponseEntity.ok(emitterTypeService.getAllEmitterTypes()
                        .stream()
                        .map(emitterType -> mapper.map(emitterType, EmitterTypeResponse.class))
                        .collect(Collectors.toList()));
    }

    @Operation(summary = "Получить список всех типов излучателей постранично",
                    description = """
                                        Принимает номер страницы, количесиво элементов на одной странице, поле и направление сортировки,
                                        возвращает страницу, содержащую список типов излучателей, и количество страниц
                                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тип излучателя найден",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = PagedResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Передан некорректный параметр",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ParameterValidationErrorResponse.class))})
    })
    @GetMapping
    public ResponseEntity<PagedResponse<EmitterTypeResponse>> getAllEmitterTypesPageable(
                    @RequestParam(value = "page", required = false) String page,
                    @RequestParam(value = "size", required = false) String size,
                    @RequestParam(value = "field", required = false) String field,
                    @RequestParam(value = "direction", required = false) String direction) {

        List<EmitterTypeResponse> emitterTypesList = new ArrayList<>();

        Page<EmitterType> responsePage = emitterTypeService.getEmitterTypesPage(
                        pageUtils.getPageNumber(page),
                        pageUtils.getPageSize(size),
                        pageUtils.getPageSortField(field),
                        pageUtils.getPageSortDirection(direction));

        responsePage.forEach(emitterType -> emitterTypesList.add(mapper.map(emitterType, EmitterTypeResponse.class)));

        PagedResponse<EmitterTypeResponse> response = new PagedResponse<>(
                        responsePage.getNumber(),
                        responsePage.getTotalPages(),
                        emitterTypesList);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить тип излучателя по идентификатору",
                    description = "Возвращает объект типа излучателя по его уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тип излучателя найден",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = EmitterTypeResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Тип излучателя не найден по заданному идентификатору",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ServerErrorResponse.class))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmitterTypeResponse> getEmitterTypeById(
                    @Parameter(description = "ID типа излучателя", example = "1") @PathVariable("id") Long id) {
        return ResponseEntity.ok(mapper.map(emitterTypeService.getEmitterTypeById(id), EmitterTypeResponse.class));
    }

    @Operation(summary = "Создать новый тип излучателя",
                    description = "Принимает запрос на создание типа излучателя, создает тип излучателя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Тип излучателя создан",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = EmitterTypeResponse.class))}),
            @ApiResponse(responseCode = "409",
                            description = "Невозможно обновить тип излучателя, тип излучателя с заданным именем уже существует",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ServerErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Невозможно создать тип излучателя, ошибка валидации",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = FormValidationErrorResponse.class))})
    })
    @PostMapping
    public ResponseEntity<EmitterTypeResponse> createEmitterType(
                    @RequestBody @Valid EmitterTypeRequest emitterTypeRequest,
                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            Map<String, List<String>> errorMessages = ValidationUtils.getErrorMessages(errors);
            throw new FormValidationException(HttpStatus.BAD_REQUEST, errorMessages, Instant.now());
        }

        EmitterType emitterType = emitterTypeService
                        .createEmitterType(mapper.map(emitterTypeRequest, EmitterType.class));

        return new ResponseEntity<>(mapper.map(emitterType, EmitterTypeResponse.class), HttpStatus.CREATED);

    }

    @Operation(summary = "Обновить тип излучателя",
                    description = "Принимает запрос на обновление типа излучателя и его идентификатор, обновляет тип излучателя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тип излучателя обновлен",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = EmitterTypeResponse.class))}),
            @ApiResponse(responseCode = "409",
                            description = "Невозможно обновить тип излучателя, тип излучателя с заданным именем уже существует",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ServerErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Невозможно обновить тип излучателя, ошибка валидации",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = FormValidationErrorResponse.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmitterTypeResponse> editEmitterType(
                    @Parameter(description = "ID типа излучателя",
                                    example = "1") @PathVariable("id") Long id,
                    @RequestBody @Valid EmitterTypeRequest emitterTypeRequest,
                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            Map<String, List<String>> errorMessages = ValidationUtils.getErrorMessages(errors);
            throw new FormValidationException(HttpStatus.BAD_REQUEST, errorMessages, Instant.now());
        }

        EmitterType emitterTypeToUpdate = mapper.map(emitterTypeRequest, EmitterType.class);

        EmitterType updatedEmitterType = emitterTypeService.updateEmitterType(id, emitterTypeToUpdate);

        return ResponseEntity.ok(mapper.map(updatedEmitterType, EmitterTypeResponse.class));
    }

    @Operation(summary = "Удалить тип излучателя по идентификатору",
                    description = "Удаляет объект типа излучателя по его уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тип излучателя найден",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = EmitterTypeResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Тип излучателя не найден по заданному идентификатору",
                            content = {@Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ServerErrorResponse.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmitterType(@PathVariable("id") Long id) {
        emitterTypeService.deleteEmitterType(id);
        return ResponseEntity.ok(String.format("Тип излучателя с идентификатором %d успешно удален", id));
    }
}
