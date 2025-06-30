package com.checkproof.explore.ai_tools_java_cursor.util;

import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for calculating recurrence patterns and generating recurring task instances
 */
@Component
@Slf4j
public class RecurrenceCalculator {

    /**
     * Calculate the next occurrence based on recurrence pattern
     */
    public LocalDateTime calculateNextOccurrence(LocalDateTime currentDate, RecurrencePattern pattern) {
        if (pattern == null || pattern.getRecurrenceType() == null) {
            throw new IllegalArgumentException("Recurrence pattern and type cannot be null");
        }

        log.debug("Calculating next occurrence for date: {} with pattern: {}", currentDate, pattern.getRecurrenceType());

        switch (pattern.getRecurrenceType()) {
            case DAILY:
                return currentDate.plusDays(pattern.getInterval());
            case WEEKLY:
                return currentDate.plusWeeks(pattern.getInterval());
            case MONTHLY:
                return currentDate.plusMonths(pattern.getInterval());
            case YEARLY:
                return currentDate.plusYears(pattern.getInterval());
            default:
                throw new IllegalArgumentException("Unsupported recurrence type: " + pattern.getRecurrenceType());
        }
    }

    /**
     * Calculate the next occurrence considering specific days of week
     */
    public LocalDateTime calculateNextOccurrenceWithDaysOfWeek(LocalDateTime currentDate, RecurrencePattern pattern) {
        if (pattern.getDaysOfWeek() == null || pattern.getDaysOfWeek().isEmpty()) {
            return calculateNextOccurrence(currentDate, pattern);
        }

        LocalDateTime nextDate = currentDate;
        int attempts = 0;
        int maxAttempts = 10; // Prevent infinite loops

        while (attempts < maxAttempts) {
            nextDate = calculateNextOccurrence(nextDate, pattern);
            
            if (pattern.getDaysOfWeek().contains(nextDate.getDayOfWeek())) {
                return nextDate;
            }
            
            attempts++;
        }

        log.warn("Could not find next occurrence within {} attempts for pattern: {}", maxAttempts, pattern.getRecurrenceType());
        return nextDate;
    }

    /**
     * Calculate the next occurrence for monthly patterns with specific day of month
     */
    public LocalDateTime calculateNextMonthlyOccurrence(LocalDateTime currentDate, RecurrencePattern pattern) {
        if (pattern.getDayOfMonth() != null) {
            return calculateNextMonthlyWithDayOfMonth(currentDate, pattern);
        } else if (pattern.getWeekOfMonth() != null) {
            return calculateNextMonthlyWithWeekOfMonth(currentDate, pattern);
        } else {
            return calculateNextOccurrence(currentDate, pattern);
        }
    }

    /**
     * Calculate next monthly occurrence with specific day of month
     */
    private LocalDateTime calculateNextMonthlyWithDayOfMonth(LocalDateTime currentDate, RecurrencePattern pattern) {
        LocalDateTime nextDate = currentDate.plusMonths(pattern.getInterval());
        
        // Try to set the day of month, handling invalid dates
        try {
            return nextDate.withDayOfMonth(pattern.getDayOfMonth());
        } catch (Exception e) {
            // If the day doesn't exist in the month, use the last day of the month
            log.debug("Day {} does not exist in month, using last day of month", pattern.getDayOfMonth());
            return nextDate.withDayOfMonth(nextDate.toLocalDate().lengthOfMonth());
        }
    }

    /**
     * Calculate next monthly occurrence with specific week of month
     */
    private LocalDateTime calculateNextMonthlyWithWeekOfMonth(LocalDateTime currentDate, RecurrencePattern pattern) {
        LocalDateTime nextDate = currentDate.plusMonths(pattern.getInterval());
        
        // This is a simplified implementation - you might want to add more logic
        // for handling specific weeks of the month
        return nextDate;
    }

    /**
     * Calculate the end date for a recurring task instance
     */
    public LocalDateTime calculateEndDate(LocalDateTime startDate, LocalDateTime originalStartDate, LocalDateTime originalEndDate) {
        if (originalEndDate == null) {
            return startDate.plusHours(1); // Default 1 hour duration
        }
        
        long durationInMinutes = java.time.Duration.between(originalStartDate, originalEndDate).toMinutes();
        return startDate.plusMinutes(durationInMinutes);
    }

    /**
     * Check if a date falls within the recurrence pattern's active period
     */
    public boolean isDateInActivePeriod(LocalDateTime date, RecurrencePattern pattern) {
        if (pattern.getStartDate() != null && date.toLocalDate().isBefore(pattern.getStartDate())) {
            return false;
        }
        
        if (pattern.getEndDate() != null && date.toLocalDate().isAfter(pattern.getEndDate())) {
            return false;
        }
        
        return true;
    }

    /**
     * Generate a list of dates for recurring tasks based on pattern
     */
    public List<LocalDateTime> generateRecurrenceDates(LocalDateTime startDate, RecurrencePattern pattern, int maxOccurrences) {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime currentDate = startDate;
        int occurrenceCount = 0;
        
        LocalDateTime endDate = pattern.getEndDate() != null ? 
            pattern.getEndDate().atStartOfDay() : 
            LocalDateTime.now().plusYears(1); // Default to 1 year if no end date
        
        int maxOccurrencesToGenerate = Math.min(maxOccurrences, 
            pattern.getMaxOccurrences() != null ? pattern.getMaxOccurrences() : Integer.MAX_VALUE);
        
        while (currentDate.isBefore(endDate) && occurrenceCount < maxOccurrencesToGenerate) {
            if (isDateInActivePeriod(currentDate, pattern)) {
                dates.add(currentDate);
                occurrenceCount++;
            }
            
            currentDate = calculateNextOccurrence(currentDate, pattern);
        }
        
        log.debug("Generated {} recurrence dates for pattern: {}", dates.size(), pattern.getRecurrenceType());
        return dates;
    }

    /**
     * Validate recurrence pattern
     */
    public void validateRecurrencePattern(RecurrencePattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Recurrence pattern cannot be null");
        }
        
        if (pattern.getRecurrenceType() == null) {
            throw new IllegalArgumentException("Recurrence type cannot be null");
        }
        
        if (pattern.getInterval() == null || pattern.getInterval() < 1) {
            throw new IllegalArgumentException("Interval must be at least 1");
        }
        
        if (pattern.getStartDate() != null && pattern.getEndDate() != null && 
            pattern.getEndDate().isBefore(pattern.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        if (pattern.getMaxOccurrences() != null && pattern.getMaxOccurrences() < 1) {
            throw new IllegalArgumentException("Max occurrences must be at least 1");
        }
    }
} 