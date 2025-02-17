package ru.klokov.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;

import ru.klokov.backend.model.EmitterOwner;

public interface EmitterOwnerService {
    List<EmitterOwner> getAllOwners();

    EmitterOwner getOwnerById(Long id);

    Page<EmitterOwner> getOwnersPage(int pageNumber, int pageSize, String sortField, boolean sortAsc);

    EmitterOwner createOwner(EmitterOwner owner);

    EmitterOwner updateOwner(Long id, EmitterOwner owner);

    void deleteOwner(Long id);
}
