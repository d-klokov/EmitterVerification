package ru.klokov.backend.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.klokov.backend.dto.PagedResponse;
import ru.klokov.backend.dto.emittertype.EmitterTypeRequest;
import ru.klokov.backend.dto.emittertype.EmitterTypeResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.service.EmitterTypeService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/types")
@RequiredArgsConstructor
public class EmitterTypeController {

    private final EmitterTypeService emitterTypeService;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<PagedResponse<EmitterTypeResponse>> getAllEmitterTypes(
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "field", required = false) String field,
            @RequestParam(value = "asc", required = false) String asc) {

        List<EmitterTypeResponse> emitterTypesList = new ArrayList<>();

        int pageNumber = 1;
        int pageSize = 5;
        String sortField = "id";
        boolean sortDirection = true;

        if (page != null && !page.isBlank())
            pageNumber = Integer.parseInt(page);
        if (size != null && !size.isBlank())
            pageSize = Integer.parseInt(size);
        if (field != null && !field.isBlank())
            sortField = String.valueOf(sortField);
        if (asc != null && !asc.isBlank())
            sortDirection = Boolean.parseBoolean(asc);

        Page<EmitterType> responsePage = emitterTypeService.getEmitterTypesPage(pageNumber, pageSize, sortField,
                sortDirection);

        responsePage
                .forEach(emitterType -> emitterTypesList.add(mapper.map(emitterType, EmitterTypeResponse.class)));

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

        EmitterType emitterTypeToUpdate = emitterTypeService.getEmitterTypeById(id);

        emitterTypeToUpdate.setName(emitterTypeRequest.getName());

        EmitterType updatedEmitterType = emitterTypeService.updateEmitterType(id, emitterTypeToUpdate);

        return ResponseEntity.ok(mapper.map(updatedEmitterType, EmitterTypeResponse.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmitterType(@PathVariable("id") Long id) {
        emitterTypeService.deleteEmitterType(id);
        return ResponseEntity.ok(String.format("Тип излучателя с id = %d успешно удален", id));
    }

}
