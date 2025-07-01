package com.checkproof.explore.ai_tools_java_cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for time slot availability checking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotAvailabilityResponse {
    private boolean isAvailable;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
} 