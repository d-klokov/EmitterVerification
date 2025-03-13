package ru.klokov.backend.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.repository.EmitterTypeRepository;
import ru.klokov.backend.service.EmitterTypeService;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultEmitterTypeService implements EmitterTypeService {

    private final EmitterTypeRepository emitterTypeRepository;

    @Override
    public List<EmitterType> getAllEmitterTypes() {
        return emitterTypeRepository.findAll();
    }

    @Override
    public EmitterType getEmitterTypeById(Long id) {

        log.info("Method getEmitterTypeById executed with parameter {}", id);

        return emitterTypeRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Тип излучателя с идентификатором %d не найден", id),
                        Instant.now()
                )
        );
    }

    @Override
    public Page<EmitterType> getEmitterTypesPage(int pageNumber, int pageSize, String sortField, boolean sortAsc) {

        Sort sort = sortAsc ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        log.info("Method getEmitterTypesPage executed with parameter {}", pageNumber);

        return emitterTypeRepository.findAll(pageable);

    }

    @Override
    public EmitterType createEmitterType(EmitterType emitterType) {

        log.info("Method createEmitterType executed with parameter {}", emitterType);

        try {
            return emitterTypeRepository.save(emitterType);
        } catch (DataIntegrityViolationException  exception) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    String.format("Тип излучателя с названием '%s' уже существует", emitterType.getName()),
                    Instant.now());
        }

    }

    @Override
    public EmitterType updateEmitterType(Long id, EmitterType emitterType) {

        log.info("Method updateEmitterType executed with parameters {}, {}", emitterType, id);

        EmitterType emitterTypeToUpdate = emitterTypeRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Emitter type with id=%d not found", id),
                        Instant.now()));

        emitterTypeToUpdate.setName(emitterType.getName());

        return createEmitterType(emitterTypeToUpdate);

    }

    @Override
    public void deleteEmitterType(Long id) {

        log.info("Method deleteEmitterType executed with parameter {}", id);

        emitterTypeRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Тип излучателя с идентификатором %d не найден", id),
                        Instant.now()));

        emitterTypeRepository.deleteById(id);

    }

}
