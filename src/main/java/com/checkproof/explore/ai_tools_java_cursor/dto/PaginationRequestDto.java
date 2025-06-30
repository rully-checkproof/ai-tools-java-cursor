package com.checkproof.explore.ai_tools_java_cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Pagination request parameters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationRequestDto {

    @Min(value = 0, message = "Page number must be 0 or greater")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size = 20;

    private String sortBy;
    private String sortDirection = "ASC";

    /**
     * Convert to Spring Data Pageable
     */
    public Pageable toPageable() {
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), sortBy);
            return PageRequest.of(page, size, sort);
        }
        return PageRequest.of(page, size);
    }

    /**
     * Convert to Spring Data Pageable with default sort
     */
    public Pageable toPageable(String defaultSortBy) {
        String sortField = sortBy != null && !sortBy.trim().isEmpty() ? sortBy : defaultSortBy;
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), sortField);
        return PageRequest.of(page, size, sort);
    }

    /**
     * Get the offset for manual pagination
     */
    public long getOffset() {
        return (long) page * size;
    }

    /**
     * Check if sorting is requested
     */
    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    /**
     * Get sort direction as enum
     */
    public Sort.Direction getSortDirectionEnum() {
        return Sort.Direction.fromString(sortDirection.toUpperCase());
    }

    /**
     * Create with default values
     */
    public static PaginationRequestDto ofDefault() {
        return PaginationRequestDto.builder()
                .page(0)
                .size(20)
                .sortDirection("ASC")
                .build();
    }

    /**
     * Create with custom page and size
     */
    public static PaginationRequestDto of(int page, int size) {
        return PaginationRequestDto.builder()
                .page(page)
                .size(size)
                .sortDirection("ASC")
                .build();
    }

    /**
     * Create with custom page, size, and sorting
     */
    public static PaginationRequestDto of(int page, int size, String sortBy, String sortDirection) {
        return PaginationRequestDto.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
    }
} 