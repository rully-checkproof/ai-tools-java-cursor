package com.checkproof.explore.ai_tools_java_cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for event conflict checking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConflictCheckResponse {
    private boolean hasConflict;
    private List<EventDto> conflictingEvents;
} 