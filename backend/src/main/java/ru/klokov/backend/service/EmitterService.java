package ru.klokov.backend.service;

import org.springframework.data.domain.Page;
import ru.klokov.backend.model.Emitter;

import java.util.List;

public interface EmitterService {
    List<Emitter> getAllEmitters();
    Emitter getEmitterById(Long id);
    Page<Emitter> getEmittersPage(int pageNumber);
    Emitter createEmitter(Emitter emitter);
    Emitter updateEmitter(Long id, Emitter emitter);
    void deleteEmitter(Long id);
}
