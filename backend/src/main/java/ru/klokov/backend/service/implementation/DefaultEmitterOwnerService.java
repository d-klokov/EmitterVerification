package ru.klokov.backend.service.implementation;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.EmitterOwner;
import ru.klokov.backend.repository.EmitterOwnerRepository;
import ru.klokov.backend.service.EmitterOwnerService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultEmitterOwnerService implements EmitterOwnerService {

    private final EmitterOwnerRepository ownerRepository;

    @Value("${page.size}")
    private int pageSize;

    @Override
    public List<EmitterOwner> getAllOwners() {
        return ownerRepository.findAll();
    }

    @Override
    public EmitterOwner getOwnerById(Long id) {

        log.info("Method getOwnerById executed with parameter {}", id);

        return ownerRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Владелец излучателя с идентификатором %d не найден", id),
                        Instant.now()));
    }

    @Override
    public Page<EmitterOwner> getOwnersPage(int pageNumber, int pageSize, String sortField, boolean sortAsc) {

        Sort sort = sortAsc ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        log.info("Method getOwnersPage executed with parameter {}", pageNumber);

        return ownerRepository.findAll(pageable);

    }

    @Override
    public EmitterOwner createOwner(EmitterOwner owner) {

        log.info("Method createOwner executed with parameter {}", owner);

        try {
            return ownerRepository.save(owner);
        } catch (DataIntegrityViolationException  exception) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    String.format("Владелец излучателя с именем '%s' уже существует", owner.getName()),
                    Instant.now());
        }

    }

    @Override
    public EmitterOwner updateOwner(Long id, EmitterOwner owner) {

        log.info("Method updateOwner executed with parameters {}, {}", owner, id);

        EmitterOwner ownerToUpdate = ownerRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Владелец излучателя с идентификатором %d не найден", id),
                        Instant.now()));

        ownerToUpdate.setName(owner.getName());

        return createOwner(ownerToUpdate);

    }

    @Override
    public void deleteOwner(Long id) {

        log.info("Method deleteOwner executed with parameter {}", id);

        ownerRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Владелец излучателя с идентификатором %d не найден", id),
                        Instant.now()));

        ownerRepository.deleteById(id);

    }

}
