package ru.klokov.backend.dto.emitter;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EmitterRequest {
    @NotBlank(message = "Заполните поле \"заводской номер\"!")
    @Size(min = 2, max = 10, message = "Заводской номер излучателя должен состоять минимум из 2 и максимум из 10 символов!")
    private String factoryNumber;

    @NotNull(message = "Заполните поле \"дата изготовления\"!")
    @Past(message = "Проверьте корректность выбранной даты!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate manufactureDate;

    @NotNull(message = "Заполните поле \"периодичность проверки\"!")
    private Integer verificationPeriodicityInMonths;

    @NotBlank(message = "Заполните поле \"для внутреннего/внешнего использования\"!")
    private Boolean forExternalUse;

    @NotBlank(message = "Заполните поле \"наличие внутреннего генератора\"!")
    private Boolean hasInternalGenerator;

    @NotNull(message = "Заполните поле \"минимальная длительность импульса\"!")
    private Double minimumPulseWidth;
    @NotNull(message = "Заполните поле \"максимальная длительность импульса\"!")
    private Double maximumPulseWidth;

    @NotNull(message = "Заполните поле \"минимальная частота импульса (10 Гц)\"!")
    private Double minimumPulseFrequency10;
    @NotNull(message = "Заполните поле \"максимальная частота импульса (10 Гц)\"!")
    private Double maximumPulseFrequency10;

    @NotNull(message = "Заполните поле \"минимальная частота импульса (100 Гц)\"!")
    private Double minimumPulseFrequency100;
    @NotNull(message = "Заполните поле \"максимальная частота импульса (100 Гц)\"!")
    private Double maximumPulseFrequency100;

    @NotNull(message = "Заполните поле \"минимальная частота импульса (1000 Гц)\"!")
    private Double minimumPulseFrequency1000;
    @NotNull(message = "Заполните поле \"максимальная частота импульса (1000 Гц)\"!")
    private Double maximumPulseFrequency1000;

    @NotNull(message = "Заполните поле \"минимальное значение импульсной мощности\"!")
    private Double minimumPulsePower;
    @NotNull(message = "Заполните поле \"максимальное значение импульсной мощности\"!")
    private Double maximumPulsePower;

    @NotNull(message = "Заполните поле \"минимальное значение угла расходимости потока излучения\"!")
    private Double minimumRadiationFluxDivergenceAngle;
    @NotNull(message = "Заполните поле \"максимальное значение угла расходимости потока излучения\"!")
    private Double maximumRadiationFluxDivergenceAngle;

    @NotNull(message = "Заполните поле \"минимальное значение непараллельности оптической и строительной оси\"!")
    private Double minimumNonParallelismOfTheOpticalAndConstructionAxis;
    @NotNull(message = "Заполните поле \"максимальное значение непараллельности оптической и строительной оси\"!")
    private Double maximumNonParallelismOfTheOpticalAndConstructionAxis;

    @NotNull(message = "Заполните поле \"минимальное значение неравномерности потока излучения\"!")
    private Double minimumUnevennessOfRadiationFLux;
    @NotNull(message = "Заполните поле \"максимальное значение неравномерности потока излучения\"!")
    private Double maximumUnevennessOfRadiationFLux;

    @NotNull(message = "Выберите тип излучателя!")
    private Long emitterTypeId;

    @NotNull(message = "Выберите владельца излучателя!")
    private Long emitterOwnerId;
}
