package ru.klokov.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.klokov.backend.dto.PagedResponse;
import ru.klokov.backend.dto.emittertype.EmitterTypeRequest;
import ru.klokov.backend.dto.emittertype.EmitterTypeResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.service.EmitterTypeService;
import ru.klokov.backend.utils.PageUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/types")
@RequiredArgsConstructor
public class EmitterTypeController {

    private final EmitterTypeService emitterTypeService;
    private final ModelMapper mapper;
    private final PageUtils pageUtils;

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
                pageUtils.getPageSortDirection(direction)
        );

        responsePage.forEach(emitterType ->
                emitterTypesList.add(mapper.map(emitterType, EmitterTypeResponse.class)));

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
            FieldError error = bindingResult.getFieldError("name");

            if (error != null)
                throw new ApiException(HttpStatus.BAD_REQUEST, error.getDefaultMessage(), Instant.now());
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
            FieldError error = bindingResult.getFieldError("name");

            if (error != null)
                throw new ApiException(HttpStatus.BAD_REQUEST, error.getDefaultMessage(), Instant.now());
        }

        EmitterType emitterTypeToUpdate = mapper.map(emitterTypeRequest, EmitterType.class);

        EmitterType updatedEmitterType = emitterTypeService.updateEmitterType(id, emitterTypeToUpdate);

        return ResponseEntity.ok(mapper.map(updatedEmitterType, EmitterTypeResponse.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmitterType(@PathVariable("id") Long id) {
        emitterTypeService.deleteEmitterType(id);
        return ResponseEntity.ok(String.format("Тип излучателя с id = %d успешно удален", id));
    }

}
