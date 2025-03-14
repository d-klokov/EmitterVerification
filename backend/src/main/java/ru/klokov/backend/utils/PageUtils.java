package ru.klokov.backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PageUtils {

    @Value("${page.default.number}")
    private int PAGE_NUMBER_DEFAULT_VALUE;

    @Value("${page.default.size}")
    private int PAGE_SIZE_DEFAULT_VALUE;

    @Value("${page.default.sort.field}")
    private String PAGE_SORT_FIELD_DEFAULT_VALUE;

    @Value("${page.default.sort.direction}")
    private boolean PAGE_SORT_FIELD_DIRECTION_DEFAULT_VALUE;

    public int getPageNumber(String pageNumberParam) {
        if (pageNumberParam != null && !pageNumberParam.isBlank())
            return Integer.parseInt(pageNumberParam);

        return PAGE_NUMBER_DEFAULT_VALUE;
    }

    public int getPageSize(String pageSizeParam) {
        if (pageSizeParam != null && !pageSizeParam.isBlank())
            return Integer.parseInt(pageSizeParam);

        return PAGE_SIZE_DEFAULT_VALUE;
    }

    public String getPageSortField(String pageSortFieldParam) {
        if (pageSortFieldParam != null && !pageSortFieldParam.isBlank())
            return pageSortFieldParam;

        return PAGE_SORT_FIELD_DEFAULT_VALUE;
    }

    public boolean getPageSortDirection(String pageSortDirectionParam) {
        if (pageSortDirectionParam != null && !pageSortDirectionParam.isBlank())
            return Boolean.parseBoolean(pageSortDirectionParam);

        return PAGE_SORT_FIELD_DIRECTION_DEFAULT_VALUE;
    }
}
