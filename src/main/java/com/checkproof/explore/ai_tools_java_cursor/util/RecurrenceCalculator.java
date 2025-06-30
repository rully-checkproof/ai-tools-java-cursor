package com.checkproof.explore.ai_tools_java_cursor.util;

import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
                return calculateNextWeeklyOccurrence(currentDate, pattern);
            case MONTHLY:
                return calculateNextMonthlyOccurrence(currentDate, pattern);
            case YEARLY:
                return currentDate.plusYears(pattern.getInterval());
            default:
                throw new IllegalArgumentException("Unsupported recurrence type: " + pattern.getRecurrenceType());
        }
    }

    /**
     * Calculate the next weekly occurrence considering specific days of week
     */
    private LocalDateTime calculateNextWeeklyOccurrence(LocalDateTime currentDate, RecurrencePattern pattern) {
        if (pattern.getDaysOfWeek() == null || pattern.getDaysOfWeek().isEmpty()) {
            return currentDate.plusWeeks(pattern.getInterval());
        }

        // Find the next occurrence among the specified days of the week
        LocalDateTime nextDate = currentDate.plusDays(1); // Start from next day
        int attempts = 0;
        int maxAttempts = 7 * pattern.getInterval(); // Maximum attempts to prevent infinite loops

        while (attempts < maxAttempts) {
            if (pattern.getDaysOfWeek().contains(nextDate.getDayOfWeek())) {
                return nextDate;
            }
            nextDate = nextDate.plusDays(1);
            attempts++;
        }

        log.warn("Could not find next weekly occurrence within {} attempts", maxAttempts);
        return currentDate.plusWeeks(pattern.getInterval());
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
        } else if (pattern.getWeekOfMonth() != null && pattern.getDaysOfWeek() != null && !pattern.getDaysOfWeek().isEmpty()) {
            return calculateNextMonthlyWithWeekOfMonth(currentDate, pattern);
        } else {
            return currentDate.plusMonths(pattern.getInterval());
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
     * Calculate next monthly occurrence with specific week of month and days of week
     */
    private LocalDateTime calculateNextMonthlyWithWeekOfMonth(LocalDateTime currentDate, RecurrencePattern pattern) {
        LocalDateTime nextDate = currentDate.plusMonths(pattern.getInterval());
        
        if (pattern.getWeekOfMonth() == null || pattern.getDaysOfWeek() == null || pattern.getDaysOfWeek().isEmpty()) {
            throw new UnsupportedOperationException("Week-of-month and days-of-week must be specified for this recurrence pattern.");
        }
        
        // Calculate the first day of the target month
        LocalDateTime firstDayOfMonth = nextDate.withDayOfMonth(1);
        
        // Find the first occurrence of any of the specified days of the week in the month
        LocalDateTime firstOccurrence = firstDayOfMonth;
        while (!pattern.getDaysOfWeek().contains(firstOccurrence.getDayOfWeek())) {
            firstOccurrence = firstOccurrence.plusDays(1);
            
            // Safety check to prevent infinite loop
            if (firstOccurrence.getMonth() != firstDayOfMonth.getMonth()) {
                throw new UnsupportedOperationException("No matching days of week found in the target month.");
            }
        }
        
        // Calculate the target week
        int targetWeek = pattern.getWeekOfMonth();
        LocalDateTime targetDate = firstOccurrence.plusWeeks(targetWeek - 1);
        
        // Ensure the target date is within the same month
        if (targetDate.getMonth() != nextDate.getMonth()) {
            throw new UnsupportedOperationException("Specified week-of-month exceeds the number of weeks in the month.");
        }
        
        return targetDate;
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
     * Generate multiple occurrences for weekly patterns with multiple days
     */
    public List<LocalDateTime> generateWeeklyRecurrenceDates(LocalDateTime startDate, RecurrencePattern pattern, int maxOccurrences) {
        List<LocalDateTime> dates = new ArrayList<>();
        Set<DayOfWeek> daysOfWeek = pattern.getDaysOfWeek();
        
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            return generateRecurrenceDates(startDate, pattern, maxOccurrences);
        }
        
        LocalDateTime currentDate = startDate;
        int occurrenceCount = 0;
        
        LocalDateTime endDate = pattern.getEndDate() != null ? 
            pattern.getEndDate().atStartOfDay() : 
            LocalDateTime.now().plusYears(1);
        
        int maxOccurrencesToGenerate = Math.min(maxOccurrences, 
            pattern.getMaxOccurrences() != null ? pattern.getMaxOccurrences() : Integer.MAX_VALUE);
        
        while (currentDate.isBefore(endDate) && occurrenceCount < maxOccurrencesToGenerate) {
            // Add occurrences for each specified day of the week in the current week
            for (DayOfWeek dayOfWeek : daysOfWeek) {
                LocalDateTime occurrenceDate = findNextDayOfWeek(currentDate, dayOfWeek);
                
                if (occurrenceDate.isBefore(endDate) && occurrenceCount < maxOccurrencesToGenerate) {
                    if (isDateInActivePeriod(occurrenceDate, pattern)) {
                        dates.add(occurrenceDate);
                        occurrenceCount++;
                    }
                }
            }
            
            // Move to next week
            currentDate = currentDate.plusWeeks(pattern.getInterval());
        }
        
        log.debug("Generated {} weekly recurrence dates for pattern: {}", dates.size(), pattern.getRecurrenceType());
        return dates;
    }

    /**
     * Find the next occurrence of a specific day of the week from a given date
     */
    private LocalDateTime findNextDayOfWeek(LocalDateTime fromDate, DayOfWeek targetDayOfWeek) {
        LocalDateTime current = fromDate;
        int attempts = 0;
        
        while (current.getDayOfWeek() != targetDayOfWeek && attempts < 7) {
            current = current.plusDays(1);
            attempts++;
        }
        
        return current;
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
        
        // Validate weekly patterns with days of week
        if (pattern.getRecurrenceType() == RecurrencePattern.RecurrenceType.WEEKLY && 
            (pattern.getDaysOfWeek() == null || pattern.getDaysOfWeek().isEmpty())) {
            log.warn("Weekly recurrence pattern should specify days of week for better control");
        }
    }
} 