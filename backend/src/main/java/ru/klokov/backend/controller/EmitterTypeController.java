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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.klokov.backend.dto.PagedResponse;
import ru.klokov.backend.dto.emittertype.EmitterTypeRequest;
import ru.klokov.backend.dto.emittertype.EmitterTypeResponse;
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

    @GetMapping("/all")
    public ResponseEntity<List<EmitterTypeResponse>> getAllEmitterTypes() {
        return ResponseEntity.ok(emitterTypeService.getAllEmitterTypes()
                .stream()
                .map(emitterType -> mapper.map(emitterType, EmitterTypeResponse.class))
                .collect(Collectors.toList()));
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<EmitterTypeResponse> getEmitterTypeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mapper.map(emitterTypeService.getEmitterTypeById(id), EmitterTypeResponse.class));
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<EmitterTypeResponse> editEmitterType(@PathVariable("id") Long id,
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmitterType(@PathVariable("id") Long id) {
        emitterTypeService.deleteEmitterType(id);
        return ResponseEntity.ok(String.format("Тип излучателя с идентификатором %d успешно удален", id));
    }
}
