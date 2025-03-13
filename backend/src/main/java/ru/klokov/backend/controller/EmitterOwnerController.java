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
import ru.klokov.backend.dto.emitterowner.EmitterOwnerRequest;
import ru.klokov.backend.dto.emitterowner.EmitterOwnerResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.service.EmitterOwnerService;
import ru.klokov.backend.utils.PageUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class EmitterOwnerController {

    private final EmitterOwnerService emitterOwnerService;
    private final ModelMapper mapper;
    private final PageUtils pageUtils;

    @GetMapping
    public ResponseEntity<PagedResponse<EmitterOwnerResponse>> getAllEmitterOwners(
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "field", required = false) String field,
            @RequestParam(value = "direction", required = false) String direction) {

        List<EmitterOwnerResponse> emitterOwnersList = new ArrayList<>();

        Page<EmitterOwner> responsePage = emitterOwnerService.getEmitterOwnersPage(
                pageUtils.getPageNumber(page),
                pageUtils.getPageSize(size),
                pageUtils.getPageSortField(field),
                pageUtils.getPageSortDirection(direction)
        );

        responsePage.forEach(emitterOwner ->
                emitterOwnersList.add(mapper.map(emitterOwner, EmitterOwnerResponse.class)));

        PagedResponse<EmitterOwnerResponse> response = new PagedResponse<>(
                responsePage.getNumber(),
                responsePage.getTotalPages(),
                emitterOwnersList);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<EmitterOwnerResponse> getOwnerById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mapper.map(emitterOwnerService.getEmitterOwnerById(id), EmitterOwnerResponse.class));
    }

    @PostMapping
    public ResponseEntity<EmitterOwnerResponse> createOwner(
            @RequestBody @Valid EmitterOwnerRequest ownerRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldError("name");

            if (error != null)
                throw new ApiException(HttpStatus.BAD_REQUEST, error.getDefaultMessage(), Instant.now());
        }

        EmitterOwner owner = emitterOwnerService
                .createEmitterOwner(mapper.map(ownerRequest, EmitterOwner.class));

        return new ResponseEntity<>(mapper.map(owner, EmitterOwnerResponse.class), HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EmitterOwnerResponse> editOwner(@PathVariable("id") Long id,
            @RequestBody @Valid EmitterOwnerRequest ownerRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldError("name");

            if (error != null)
                throw new ApiException(HttpStatus.BAD_REQUEST, error.getDefaultMessage(), Instant.now());
        }

        EmitterOwner emitterOwnerToUpdate = mapper.map(ownerRequest, EmitterOwner.class);

        EmitterOwner updatedEmitterOwner = emitterOwnerService.updateEmitterOwner(id, emitterOwnerToUpdate);

        return ResponseEntity.ok(mapper.map(updatedEmitterOwner, EmitterOwnerResponse.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOwner(@PathVariable("id") Long id) {
        emitterOwnerService.deleteEmitterOwner(id);
        return ResponseEntity.ok(String.format("Владелец с id = %d успешно удален", id));
    }

}
