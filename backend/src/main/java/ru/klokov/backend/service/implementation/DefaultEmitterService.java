package ru.klokov.backend.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.klokov.backend.exception.ApiException;
import ru.klokov.backend.model.Emitter;
import ru.klokov.backend.repository.EmitterRepository;
import ru.klokov.backend.service.EmitterService;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultEmitterService implements EmitterService {

    private final EmitterRepository emitterRepository;

    @Value("${page.size}")
    private int pageSize;

    @Override
    public List<Emitter> getAllEmitters() {
        return emitterRepository.findAll();
    }

    @Override
    public Emitter getEmitterById(Long id) {
        return emitterRepository.findById(id).orElseThrow(
                () -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Излучатель с идентификатором id=%d не найден!", id),
                        Instant.now()));
    }

    @Override
    public Page<Emitter> getEmittersPage(int pageNumber) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return emitterRepository.findAll(pageable);
    }

    @Override
    public Emitter createEmitter(Emitter emitter) {
        try {
            return emitterRepository.save(emitter);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    String.format("Излучатель с заводским номером '%s' уже существует!", emitter.getFactoryNumber()),
                    Instant.now());
        }
    }

    @Override
    public Emitter updateEmitter(Long id, Emitter emitter) {
        Emitter emitterToUpdate = emitterRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Излучатель с идентификатором id=%d не найден!", id),
                        Instant.now()));

        emitterToUpdate.setFactoryNumber(emitter.getFactoryNumber());
        emitterToUpdate.setManufactureDate(emitter.getManufactureDate());
        emitterToUpdate.setVerificationPeriodicityInMonths(emitter.getVerificationPeriodicityInMonths());
        emitterToUpdate.setForExternalUse(emitter.getForExternalUse());
        emitterToUpdate.setHasInternalGenerator(emitter.getHasInternalGenerator());

        emitterToUpdate.setMinimumPulseWidth(emitter.getMinimumPulseWidth());
        emitterToUpdate.setMaximumPulseWidth(emitter.getMaximumPulseWidth());

        emitterToUpdate.setMinimumPulseFrequency10(emitter.getMinimumPulseFrequency10());
        emitterToUpdate.setMaximumPulseFrequency10(emitter.getMaximumPulseFrequency10());

        emitterToUpdate.setMinimumPulseFrequency100(emitter.getMinimumPulseFrequency100());
        emitterToUpdate.setMaximumPulseFrequency100(emitter.getMaximumPulseFrequency100());

        emitterToUpdate.setMinimumPulseFrequency1000(emitter.getMinimumPulseFrequency1000());
        emitterToUpdate.setMaximumPulseFrequency1000(emitter.getMaximumPulseFrequency1000());

        emitterToUpdate.setMinimumPulsePower(emitter.getMinimumPulsePower());
        emitterToUpdate.setMaximumPulsePower(emitter.getMaximumPulsePower());

        emitterToUpdate.setMinimumRadiationFluxDivergenceAngle(emitter.getMinimumRadiationFluxDivergenceAngle());
        emitterToUpdate.setMaximumRadiationFluxDivergenceAngle(emitter.getMaximumRadiationFluxDivergenceAngle());

        emitterToUpdate.setMinimumNonParallelismOfTheOpticalAndConstructionAxis(
                emitter.getMinimumNonParallelismOfTheOpticalAndConstructionAxis());
        emitterToUpdate.setMaximumNonParallelismOfTheOpticalAndConstructionAxis(
                emitter.getMaximumNonParallelismOfTheOpticalAndConstructionAxis());

        emitterToUpdate.setMinimumUnevennessOfRadiationFLux(emitter.getMinimumUnevennessOfRadiationFLux());
        emitterToUpdate.setMaximumUnevennessOfRadiationFLux(emitter.getMaximumUnevennessOfRadiationFLux());

        emitterToUpdate.setEmitterType(emitter.getEmitterType());
        emitterToUpdate.setEmitterOwner(emitter.getEmitterOwner());

        // TODO: verifications
        // emitterToUpdate.setVerifications(emitter.getVerifications());

        return createEmitter(emitterToUpdate);
    }

    @Override
    public void deleteEmitter(Long id) {
        emitterRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format("Излучатель с идентификатором id=%d не найден!", id),
                        Instant.now()));

        emitterRepository.deleteById(id);
    }
}
