package com.checkproof.explore.ai_tools_java_cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic pagination response wrapper for any type of data
 * @param <T> The type of data being paginated
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponseDto<T> {

    private List<T> content;
    private PaginationMetadataDto pagination;

    /**
     * Create paginated response from Spring Data Page
     */
    public static <T> PaginatedResponseDto<T> fromPage(Page<T> page) {
        return PaginatedResponseDto.<T>builder()
                .content(page.getContent())
                .pagination(PaginationMetadataDto.builder()
                        .pageNumber(page.getNumber())
                        .pageSize(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .first(page.isFirst())
                        .last(page.isLast())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .build();
    }

    /**
     * Create paginated response from list with manual pagination info
     */
    public static <T> PaginatedResponseDto<T> fromList(List<T> content, int pageNumber, int pageSize, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        return PaginatedResponseDto.<T>builder()
                .content(content)
                .pagination(PaginationMetadataDto.builder()
                        .pageNumber(pageNumber)
                        .pageSize(pageSize)
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .first(pageNumber == 0)
                        .last(pageNumber >= totalPages - 1)
                        .hasNext(pageNumber < totalPages - 1)
                        .hasPrevious(pageNumber > 0)
                        .build())
                .build();
    }

    /**
     * Create empty paginated response
     */
    public static <T> PaginatedResponseDto<T> empty() {
        return PaginatedResponseDto.<T>builder()
                .content(List.of())
                .pagination(PaginationMetadataDto.builder()
                        .pageNumber(0)
                        .pageSize(0)
                        .totalElements(0)
                        .totalPages(0)
                        .first(true)
                        .last(true)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build())
                .build();
    }

    /**
     * Check if the response has content
     */
    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    /**
     * Get the number of items in current page
     */
    public int getContentSize() {
        return content != null ? content.size() : 0;
    }
} 