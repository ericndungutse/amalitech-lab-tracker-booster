package com.ndungutse.project_tracker.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Data
public class PageResponse<T> {
    private List<T> content;
    private Map<String, Object> pagination;

    public PageResponse(Page<T> page) {
        this.pagination = Map.of(
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "currentPage", page.getNumber() + 1,
                "pageSize", page.getSize(),
                "isFirst", page.isFirst(),
                "isLast", page.isLast(),
                "hasNext", page.hasNext(),
                "hasPrevious", page.hasPrevious()
        );
        this.content = page.getContent();
    }

}
