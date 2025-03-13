package ru.klokov.backend.service;

import org.springframework.data.domain.Page;
import ru.klokov.backend.model.EmitterType;

import java.util.List;

public interface EmitterTypeService {
    List<EmitterType> getAllEmitterTypes();

    EmitterType getEmitterTypeById(Long id);

    Page<EmitterType> getEmitterTypesPage(int pageNumber, int pageSize, String sortField, boolean sortAsc);

    EmitterType createEmitterType(EmitterType emitterType);

    EmitterType updateEmitterType(Long id, EmitterType emitterType);

    void deleteEmitterType(Long id);
}
