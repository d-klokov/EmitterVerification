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
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.repository.EmitterOwnerRepository;
import ru.klokov.backend.service.EmitterOwnerService;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultEmitterOwnerService implements EmitterOwnerService {

    private final EmitterOwnerRepository emitterOwnerRepository;

    @Override
    public List<EmitterOwner> getAllEmitterOwners() {
        return emitterOwnerRepository.findAll();
    }

    @Override
    public EmitterOwner getEmitterOwnerById(Long id) {

        log.info("Method getOwnerById executed with parameter {}", id);

        return emitterOwnerRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Владелец излучателя с идентификатором %d не найден", id),
                        Instant.now()));
    }

    @Override
    public Page<EmitterOwner> getEmitterOwnersPage(int pageNumber, int pageSize, String sortField, boolean sortAsc) {

        Sort sort = sortAsc ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        log.info("Method getOwnersPage executed with parameter {}", pageNumber);

        return emitterOwnerRepository.findAll(pageable);

    }

    @Override
    public EmitterOwner createEmitterOwner(EmitterOwner owner) {

        log.info("Method createOwner executed with parameter {}", owner);

        try {
            return emitterOwnerRepository.save(owner);
        } catch (DataIntegrityViolationException  exception) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    String.format("Владелец излучателя с именем '%s' уже существует", owner.getName()),
                    Instant.now());
        }

    }

    @Override
    public EmitterOwner updateEmitterOwner(Long id, EmitterOwner owner) {

        log.info("Method updateOwner executed with parameters {}, {}", owner, id);

        EmitterOwner ownerToUpdate = emitterOwnerRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Владелец излучателя с идентификатором %d не найден", id),
                        Instant.now()));

        ownerToUpdate.setName(owner.getName());

        return createEmitterOwner(ownerToUpdate);

    }

    @Override
    public void deleteEmitterOwner(Long id) {

        log.info("Method deleteOwner executed with parameter {}", id);

        emitterOwnerRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Владелец излучателя с идентификатором %d не найден", id),
                        Instant.now()));

        emitterOwnerRepository.deleteById(id);

    }

}
