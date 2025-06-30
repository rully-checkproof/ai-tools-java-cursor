package com.checkproof.explore.ai_tools_java_cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination metadata information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationMetadataDto {

    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    /**
     * Get the starting index of the current page (0-based)
     */
    public long getStartIndex() {
        return (long) pageNumber * pageSize;
    }

    /**
     * Get the ending index of the current page (0-based, exclusive)
     */
    public long getEndIndex() {
        return Math.min((long) (pageNumber + 1) * pageSize, totalElements);
    }

    /**
     * Check if the current page is empty
     */
    public boolean isEmpty() {
        return totalElements == 0;
    }

    /**
     * Get the number of elements in the current page
     */
    public int getCurrentPageSize() {
        if (isEmpty()) {
            return 0;
        }
        return (int) (getEndIndex() - getStartIndex());
    }

    /**
     * Get the next page number (returns -1 if no next page)
     */
    public int getNextPageNumber() {
        return hasNext ? pageNumber + 1 : -1;
    }

    /**
     * Get the previous page number (returns -1 if no previous page)
     */
    public int getPreviousPageNumber() {
        return hasPrevious ? pageNumber - 1 : -1;
    }
} 