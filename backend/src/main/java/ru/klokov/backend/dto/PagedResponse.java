package ru.klokov.backend.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Ответ c информацией о странице типов излучателей")
@Data
@AllArgsConstructor
public class PagedResponse<T> {
    @Schema(description = "Номер страницы", example = "1")
    private int pageNumber;
    @Schema(description = "Количество страниц", example = "1")
    private int totalPages;
    @Schema(description = "Список типов излучателей", example = "{id: 1, name: \"Тип №1\"}")
    private List<T> content;
}
