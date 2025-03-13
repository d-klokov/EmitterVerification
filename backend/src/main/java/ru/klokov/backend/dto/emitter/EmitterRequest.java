package ru.klokov.backend.dto.emitter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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
    @Size(min = 2, max = 255, message = "Заводской номер излучателя должен состоять минимум из 2 и максимум из 255 символов!")
    private String factoryNumber;

    @NotNull(message = "Заполните поле \"дата изготовления\"!")
    @Past(message = "Проверьте корректность выбранной даты!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate manufactureDate;

    @NotNull(message = "Заполните поле \"периодичность проверки\"!")
    private Integer verificationPeriodicityInMonths;

    @NotNull(message = "Поле \"Для внешнего использования\" не может быть пустым!")
    private Boolean forExternalUse;

    @NotNull(message = "Поле \"Есть внутренний генератор\" не может быть пустым!")
    private Boolean hasInternalGenerator;

    @NotNull(message = "Заполните поле \"минимальная длительность импульса\"!")
    private Double minimumPulseWidth;
    @NotNull(message = "Заполните поле \"максимальная длительность импульса\"!")
    private Double maximumPulseWidth;

    private Double minimumPulseFrequency10;
    private Double maximumPulseFrequency10;
    private Double minimumPulseFrequency100;
    private Double maximumPulseFrequency100;
    private Double minimumPulseFrequency1000;
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
