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
import ru.klokov.backend.dto.emitterowner.EmitterOwnerRequest;
import ru.klokov.backend.dto.emitterowner.EmitterOwnerResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.service.EmitterOwnerService;

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
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class EmitterOwnerController {

    private final EmitterOwnerService ownerService;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<PagedResponse<EmitterOwnerResponse>> getAllOwners(
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "field", required = false) String field,
            @RequestParam(value = "asc", required = false) String asc) {

        List<EmitterOwnerResponse> ownersList = new ArrayList<>();

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

        Page<EmitterOwner> responsePage = ownerService.getOwnersPage(pageNumber, pageSize, sortField, sortDirection);

        responsePage
                .forEach(owner -> ownersList.add(mapper.map(owner, EmitterOwnerResponse.class)));

        PagedResponse<EmitterOwnerResponse> response = new PagedResponse<>(
                responsePage.getNumber(),
                responsePage.getTotalPages(),
                ownersList);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<EmitterOwnerResponse> getOwnerById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mapper.map(ownerService.getOwnerById(id), EmitterOwnerResponse.class));
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

        EmitterOwner owner = ownerService
                .createOwner(mapper.map(ownerRequest, EmitterOwner.class));

        return new ResponseEntity<>(mapper.map(owner, EmitterOwnerResponse.class), HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EmitterOwnerResponse> editOwner(@PathVariable("id") Long id,
            @RequestBody EmitterOwnerRequest ownerRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldError("name");

            if (error != null)
                throw new ApiException(HttpStatus.BAD_REQUEST, error.getDefaultMessage(), Instant.now());
        }

        EmitterOwner ownerToUpdate = ownerService.getOwnerById(id);

        ownerToUpdate.setName(ownerRequest.getName());

        EmitterOwner updatedEmitterOwner = ownerService.updateOwner(id, ownerToUpdate);

        return ResponseEntity.ok(mapper.map(updatedEmitterOwner, EmitterOwnerResponse.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOwner(@PathVariable("id") Long id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok(String.format("Владелец с id = %d успешно удален", id));
    }

}
