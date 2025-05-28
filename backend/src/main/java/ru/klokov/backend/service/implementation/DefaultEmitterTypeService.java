package ru.klokov.backend.service.implementation;

import java.time.Instant;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.klokov.backend.exception.ServerException;
import ru.klokov.backend.model.EmitterType;
import ru.klokov.backend.repository.EmitterTypeRepository;
import ru.klokov.backend.service.EmitterTypeService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultEmitterTypeService implements EmitterTypeService {

    private final EmitterTypeRepository emitterTypeRepository;

    @Override
    public List<EmitterType> getAllEmitterTypes() {
        log.info("Method getAllEmitterTypes executed");

        return emitterTypeRepository.findAll();
    }

    @Override
    public EmitterType getEmitterTypeById(Long id) {
        log.info("Method getEmitterTypeById executed with parameter {}", id);

        return emitterTypeRepository.findById(id).orElseThrow(
                () -> new ServerException(
                        HttpStatus.NOT_FOUND,
                        String.format("Тип излучателя с идентификатором %d не найден", id),
                        Instant.now()));
    }

    @Override
    public Page<EmitterType> getEmitterTypesPage(int pageNumber, int pageSize, String sortField, boolean sortAsc) {
        log.info("Method getEmitterTypesPage executed with parameter {}", pageNumber);

        Sort sort = sortAsc ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return emitterTypeRepository.findAll(pageable);
    }

    @Override
    public EmitterType createEmitterType(EmitterType emitterType) {
        log.info("Method createEmitterType executed with parameter {}", emitterType);

        try {
            return emitterTypeRepository.save(emitterType);
        } catch (DataIntegrityViolationException exception) {
            throw new ServerException(
                    HttpStatus.CONFLICT,
                    String.format("Тип излучателя с названием \"%s\" уже существует", emitterType.getName()),
                    Instant.now());
        }
    }

    @Override
    public EmitterType updateEmitterType(Long id, EmitterType emitterType) {
        log.info("Method updateEmitterType executed with parameters {}, {}", emitterType, id);

        EmitterType emitterTypeToUpdate = emitterTypeRepository.findById(id).orElseThrow(
                () -> new ServerException(
                        HttpStatus.NOT_FOUND,
                        String.format("Тип излучателя с идентификатором %d не найден", id),
                        Instant.now()));

        emitterTypeToUpdate.setName(emitterType.getName());

        try {
            return emitterTypeRepository.save(emitterTypeToUpdate);
        } catch (DataIntegrityViolationException exception) {
            throw new ServerException(HttpStatus.CONFLICT,
                    String.format("Тип излучателя с названием \"%s\" уже существует", emitterType.getName()),
                    Instant.now());
        }
    }

    @Override
    public void deleteEmitterType(Long id) {
        log.info("Method deleteEmitterType executed with parameter {}", id);

        emitterTypeRepository.findById(id).orElseThrow(
                () -> new ServerException(
                        HttpStatus.NOT_FOUND,
                        String.format("Тип излучателя с идентификатором %d не найден", id),
                        Instant.now()));

        emitterTypeRepository.deleteById(id);
    }

}
