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
import ru.klokov.backend.dto.emitter.EmitterRequest;
import ru.klokov.backend.dto.emitter.EmitterResponse;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.Emitter;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.service.EmitterOwnerService;
import ru.klokov.backend.service.EmitterService;
import ru.klokov.backend.service.EmitterTypeService;
import ru.klokov.backend.utils.PageUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/emitters")
@RequiredArgsConstructor
public class EmitterController {
    private final EmitterTypeService emitterTypeService;
    private final EmitterOwnerService emitterOwnerService;
    private final EmitterService emitterService;
    private final ModelMapper mapper;
    private final PageUtils pageUtils;

    @GetMapping
    public ResponseEntity<PagedResponse<EmitterResponse>> getAllEmitters(
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "field", required = false) String field,
            @RequestParam(value = "direction", required = false) String direction) {

        List<EmitterResponse> emittersList = new ArrayList<>();

        Page<Emitter> responsePage = emitterService.getEmittersPage(
                pageUtils.getPageNumber(page),
                pageUtils.getPageSize(size),
                pageUtils.getPageSortField(field),
                pageUtils.getPageSortDirection(direction)
        );

        responsePage.forEach(emitter -> emittersList.add(mapper.map(emitter, EmitterResponse.class)));

        PagedResponse<EmitterResponse> response = new PagedResponse<>(
                responsePage.getNumber(),
                responsePage.getTotalPages(),
                emittersList);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<EmitterResponse> getEmitterById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mapper.map(emitterService.getEmitterById(id), EmitterResponse.class));
    }

    @PostMapping
    public ResponseEntity<EmitterResponse> createOwner(@RequestBody @Valid EmitterRequest emitterRequest,
                                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            if (!errors.isEmpty())
                throw new ApiException(HttpStatus.BAD_REQUEST, errors.get(0).getDefaultMessage(), Instant.now());
        }

        Emitter createdEmitter = emitterService.createEmitter(mapper.map(emitterRequest, Emitter.class));

        return new ResponseEntity<>(mapper.map(createdEmitter, EmitterResponse.class), HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EmitterResponse> editEmitter(@PathVariable("id") Long id,
                                                       @RequestBody @Valid EmitterRequest emitterRequest,
                                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            if (!errors.isEmpty())
                throw new ApiException(HttpStatus.BAD_REQUEST, errors.get(0).getDefaultMessage(), Instant.now());
        }

        System.out.println("emitterRequest: " + emitterRequest);

        EmitterType emitterType = emitterTypeService.getEmitterTypeById(emitterRequest.getEmitterTypeId());
        EmitterOwner emitterOwner = emitterOwnerService.getEmitterOwnerById(emitterRequest.getEmitterOwnerId());

        Emitter newEmitter = mapper.map(emitterRequest, Emitter.class);
        newEmitter.setEmitterType(emitterType);
        newEmitter.setEmitterOwner(emitterOwner);

        System.out.println("newEmitter: " + newEmitter);

        Emitter updatedEmitter = emitterService.updateEmitter(id, newEmitter);

        System.out.println("updatedEmitter: " + updatedEmitter);

        return ResponseEntity.ok(mapper.map(updatedEmitter, EmitterResponse.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmitter(@PathVariable("id") Long id) {
        emitterService.deleteEmitter(id);
        return ResponseEntity.ok(String.format("Излучатель с id = %d успешно удален", id));
    }
}
