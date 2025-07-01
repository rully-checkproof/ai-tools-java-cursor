package com.checkproof.explore.ai_tools_java_cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for event statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatisticsResponse {
    private long upcomingEvents;
    private long todayEvents;
    private long meetingEvents;
    private long conferenceEvents;
    private long workshopEvents;
} 