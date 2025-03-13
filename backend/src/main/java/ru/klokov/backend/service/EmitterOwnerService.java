package ru.klokov.backend.service;

import org.springframework.data.domain.Page;
import ru.klokov.backend.model.EmitterOwner;

import java.util.List;

public interface EmitterOwnerService {
    List<EmitterOwner> getAllEmitterOwners();

    EmitterOwner getEmitterOwnerById(Long id);

    Page<EmitterOwner> getEmitterOwnersPage(int pageNumber, int pageSize, String sortField, boolean sortAsc);

    EmitterOwner createEmitterOwner(EmitterOwner owner);

    EmitterOwner updateEmitterOwner(Long id, EmitterOwner owner);

    void deleteEmitterOwner(Long id);
}
