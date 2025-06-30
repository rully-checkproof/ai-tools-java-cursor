package com.checkproof.explore.ai_tools_java_cursor.dto;

import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurrencePatternDto {

    private Long id;

    @NotNull(message = "Recurrence type is required")
    private RecurrencePattern.RecurrenceType recurrenceType;

    @Min(value = 1, message = "Interval must be at least 1")
    private Integer interval;

    private LocalDate startDate;

    private LocalDate endDate;

    @Min(value = 1, message = "Max occurrences must be at least 1")
    private Integer maxOccurrences;

    private Set<DayOfWeek> daysOfWeek;

    @Min(value = 1, message = "Day of month must be between 1 and 31")
    private Integer dayOfMonth;

    @Min(value = 1, message = "Week of month must be between 1 and 5")
    private Integer weekOfMonth;

    @Min(value = 1, message = "Month of year must be between 1 and 12")
    private Integer monthOfYear;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper method to check if pattern is active
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return (startDate == null || !today.isBefore(startDate)) &&
               (endDate == null || !today.isAfter(endDate));
    }

    // Static factory method to create DTO from entity
    public static RecurrencePatternDto fromEntity(RecurrencePattern pattern) {
        if (pattern == null) {
            return null;
        }

        return RecurrencePatternDto.builder()
                .id(pattern.getId())
                .recurrenceType(pattern.getRecurrenceType())
                .interval(pattern.getInterval())
                .startDate(pattern.getStartDate())
                .endDate(pattern.getEndDate())
                .maxOccurrences(pattern.getMaxOccurrences())
                .daysOfWeek(pattern.getDaysOfWeek())
                .dayOfMonth(pattern.getDayOfMonth())
                .weekOfMonth(pattern.getWeekOfMonth())
                .monthOfYear(pattern.getMonthOfYear())
                .createdAt(pattern.getCreatedAt())
                .updatedAt(pattern.getUpdatedAt())
                .build();
    }

    // Method to convert DTO to entity
    public RecurrencePattern toEntity() {
        return RecurrencePattern.builder()
                .id(id)
                .recurrenceType(recurrenceType)
                .interval(interval)
                .startDate(startDate)
                .endDate(endDate)
                .maxOccurrences(maxOccurrences)
                .daysOfWeek(daysOfWeek)
                .dayOfMonth(dayOfMonth)
                .weekOfMonth(weekOfMonth)
                .monthOfYear(monthOfYear)
                .build();
    }
} 