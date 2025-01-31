package ru.klokov.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private int pageNumber;
    private int totalPages;
    private List<T> content;
}
