package com.checkproof.explore.ai_tools_java_cursor.util;

import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for handling recurring task logic and date generation
 * Merged with RecurrenceCalculator functionality to eliminate redundancy
 */
@Component
@Slf4j
public class RecurrenceUtil {

    /**
     * Generate recurring dates based on a recurrence pattern
     */
    public List<LocalDateTime> generateRecurringDates(LocalDateTime startDate, 
                                                    RecurrencePattern pattern, 
                                                    int numberOfOccurrences) {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime currentDate = startDate;

        for (int i = 0; i < numberOfOccurrences; i++) {
            dates.add(currentDate);
            currentDate = getNextOccurrence(currentDate, pattern);
        }

        return dates;
    }

    /**
     * Generate recurring dates within a date range
     */
    public List<LocalDateTime> generateRecurringDatesInRange(LocalDateTime startDate, 
                                                           LocalDateTime endDate, 
                                                           RecurrencePattern pattern) {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = getNextOccurrence(currentDate, pattern);
        }

        return dates;
    }

    /**
     * Generate a list of dates for recurring tasks based on pattern (from RecurrenceCalculator)
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
            
            currentDate = getNextOccurrence(currentDate, pattern);
        }
        
        log.debug("Generated {} recurrence dates for pattern: {}", dates.size(), pattern.getRecurrenceType());
        return dates;
    }

    /**
     * Generate multiple occurrences for weekly patterns with multiple days (from RecurrenceCalculator)
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
     * Get the next occurrence date based on recurrence pattern
     */
    public LocalDateTime getNextOccurrence(LocalDateTime currentDate, RecurrencePattern pattern) {
        if (pattern == null) {
            return currentDate.plusDays(1); // Default to daily
        }

        RecurrencePattern.RecurrenceType type = pattern.getRecurrenceType();
        int interval = pattern.getInterval();

        switch (type) {
            case DAILY:
                return currentDate.plusDays(interval);
            case WEEKLY:
                return getNextWeeklyOccurrence(currentDate, pattern);
            case MONTHLY:
                return getNextMonthlyOccurrence(currentDate, pattern);
            case YEARLY:
                return currentDate.plusYears(interval);
            default:
                return currentDate.plusDays(1);
        }
    }

    /**
     * Calculate the next occurrence based on recurrence pattern (from RecurrenceCalculator)
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
                return getNextWeeklyOccurrence(currentDate, pattern);
            case MONTHLY:
                return getNextMonthlyOccurrence(currentDate, pattern);
            case YEARLY:
                return currentDate.plusYears(pattern.getInterval());
            default:
                throw new IllegalArgumentException("Unsupported recurrence type: " + pattern.getRecurrenceType());
        }
    }

    /**
     * Get the next occurrence date with specific day of week for weekly recurrence
     */
    public LocalDateTime getNextWeeklyOccurrence(LocalDateTime currentDate, 
                                               DayOfWeek targetDayOfWeek, 
                                               int interval) {
        LocalDateTime nextDate = currentDate.plusWeeks(interval);
        
        // Adjust to the target day of week
        while (nextDate.getDayOfWeek() != targetDayOfWeek) {
            nextDate = nextDate.plusDays(1);
        }
        
        return nextDate;
    }

    /**
     * Calculate the next weekly occurrence considering specific days of week (from RecurrenceCalculator)
     */
    private LocalDateTime getNextWeeklyOccurrence(LocalDateTime currentDate, RecurrencePattern pattern) {
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
     * Calculate next occurrence considering specific days of week (from RecurrenceCalculator)
     */
    public LocalDateTime calculateNextOccurrenceWithDaysOfWeek(LocalDateTime currentDate, RecurrencePattern pattern) {
        if (pattern.getDaysOfWeek() == null || pattern.getDaysOfWeek().isEmpty()) {
            return getNextOccurrence(currentDate, pattern);
        }

        LocalDateTime nextDate = currentDate;
        int attempts = 0;
        int maxAttempts = 10; // Prevent infinite loops

        while (attempts < maxAttempts) {
            nextDate = getNextOccurrence(nextDate, pattern);
            
            if (pattern.getDaysOfWeek().contains(nextDate.getDayOfWeek())) {
                return nextDate;
            }
            
            attempts++;
        }

        log.warn("Could not find next occurrence within {} attempts for pattern: {}", maxAttempts, pattern.getRecurrenceType());
        return nextDate;
    }

    /**
     * Get the next occurrence date with specific day of month for monthly recurrence
     */
    public LocalDateTime getNextMonthlyOccurrence(LocalDateTime currentDate, 
                                                int dayOfMonth, 
                                                int interval) {
        LocalDateTime nextDate = currentDate.plusMonths(interval);
        
        // Adjust to the target day of month
        int targetDay = Math.min(dayOfMonth, nextDate.toLocalDate().lengthOfMonth());
        nextDate = nextDate.withDayOfMonth(targetDay);
        
        return nextDate;
    }

    /**
     * Calculate the next occurrence for monthly patterns with specific day of month (from RecurrenceCalculator)
     */
    public LocalDateTime getNextMonthlyOccurrence(LocalDateTime currentDate, RecurrencePattern pattern) {
        if (pattern.getDayOfMonth() != null) {
            return getNextMonthlyWithDayOfMonth(currentDate, pattern);
        } else if (pattern.getWeekOfMonth() != null && pattern.getDaysOfWeek() != null && !pattern.getDaysOfWeek().isEmpty()) {
            return getNextMonthlyWithWeekOfMonth(currentDate, pattern);
        } else {
            return currentDate.plusMonths(pattern.getInterval());
        }
    }

    /**
     * Calculate next monthly occurrence with specific day of month (from RecurrenceCalculator)
     */
    private LocalDateTime getNextMonthlyWithDayOfMonth(LocalDateTime currentDate, RecurrencePattern pattern) {
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
     * Calculate next monthly occurrence with specific week of month and days of week (from RecurrenceCalculator)
     */
    private LocalDateTime getNextMonthlyWithWeekOfMonth(LocalDateTime currentDate, RecurrencePattern pattern) {
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
     * Get the next occurrence date with specific week and day for monthly recurrence
     */
    public LocalDateTime getNextMonthlyOccurrenceByWeek(LocalDateTime currentDate, 
                                                      int weekOfMonth, 
                                                      DayOfWeek dayOfWeek, 
                                                      int interval) {
        LocalDateTime nextDate = currentDate.plusMonths(interval);
        
        // Get the first day of the month
        LocalDate firstDayOfMonth = nextDate.toLocalDate().withDayOfMonth(1);
        
        // Calculate the target date based on week and day
        LocalDate targetDate = firstDayOfMonth;
        
        // Find the first occurrence of the target day of week
        while (targetDate.getDayOfWeek() != dayOfWeek) {
            targetDate = targetDate.plusDays(1);
        }
        
        // Add weeks to get to the target week
        targetDate = targetDate.plusWeeks(weekOfMonth - 1);
        
        // Ensure we don't go into the next month
        if (targetDate.getMonth() != firstDayOfMonth.getMonth()) {
            targetDate = targetDate.minusWeeks(1);
        }
        
        return LocalDateTime.of(targetDate, nextDate.toLocalTime());
    }

    /**
     * Get the next occurrence date with specific month and day for yearly recurrence
     */
    public LocalDateTime getNextYearlyOccurrence(LocalDateTime currentDate, 
                                               int month, 
                                               int dayOfMonth, 
                                               int interval) {
        LocalDateTime nextDate = currentDate.plusYears(interval);
        
        // Adjust to the target month and day
        nextDate = nextDate.withMonth(month);
        
        // Handle leap year and month length issues
        int targetDay = Math.min(dayOfMonth, nextDate.toLocalDate().lengthOfMonth());
        nextDate = nextDate.withDayOfMonth(targetDay);
        
        return nextDate;
    }

    /**
     * Calculate the number of occurrences between two dates
     */
    public int calculateOccurrences(LocalDateTime startDate, 
                                  LocalDateTime endDate, 
                                  RecurrencePattern pattern) {
        if (startDate.isAfter(endDate)) {
            return 0;
        }

        int count = 0;
        LocalDateTime currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            count++;
            currentDate = getNextOccurrence(currentDate, pattern);
        }

        return count;
    }

    /**
     * Check if a date is a valid occurrence based on recurrence pattern
     */
    public boolean isValidOccurrence(LocalDateTime date, 
                                   LocalDateTime startDate, 
                                   RecurrencePattern pattern) {
        if (date.isBefore(startDate)) {
            return false;
        }

        LocalDateTime currentDate = startDate;
        
        while (currentDate.isBefore(date)) {
            currentDate = getNextOccurrence(currentDate, pattern);
        }
        
        return currentDate.equals(date);
    }

    /**
     * Get the nth occurrence from the start date
     */
    public LocalDateTime getNthOccurrence(LocalDateTime startDate, 
                                        RecurrencePattern pattern, 
                                        int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Occurrence number must be positive");
        }

        LocalDateTime currentDate = startDate;
        
        for (int i = 1; i < n; i++) {
            currentDate = getNextOccurrence(currentDate, pattern);
        }
        
        return currentDate;
    }

    /**
     * Get the last occurrence before a specific date
     */
    public LocalDateTime getLastOccurrenceBefore(LocalDateTime date, 
                                               LocalDateTime startDate, 
                                               RecurrencePattern pattern) {
        if (date.isBefore(startDate)) {
            return null;
        }

        LocalDateTime currentDate = startDate;
        LocalDateTime lastOccurrence = startDate;
        
        while (!currentDate.isAfter(date)) {
            lastOccurrence = currentDate;
            currentDate = getNextOccurrence(currentDate, pattern);
        }
        
        return lastOccurrence;
    }

    /**
     * Get the first occurrence after a specific date
     */
    public LocalDateTime getFirstOccurrenceAfter(LocalDateTime date, 
                                               LocalDateTime startDate, 
                                               RecurrencePattern pattern) {
        LocalDateTime currentDate = startDate;
        
        while (!currentDate.isAfter(date)) {
            currentDate = getNextOccurrence(currentDate, pattern);
        }
        
        return currentDate;
    }

    /**
     * Skip occurrences (useful for handling exceptions)
     */
    public LocalDateTime skipOccurrences(LocalDateTime startDate, 
                                       RecurrencePattern pattern, 
                                       int skipCount) {
        if (skipCount <= 0) {
            return startDate;
        }

        LocalDateTime currentDate = startDate;
        
        for (int i = 0; i < skipCount; i++) {
            currentDate = getNextOccurrence(currentDate, pattern);
        }
        
        return currentDate;
    }

    /**
     * Get all occurrences for a specific day of week in a date range
     */
    public List<LocalDateTime> getOccurrencesByDayOfWeek(LocalDateTime startDate, 
                                                       LocalDateTime endDate, 
                                                       DayOfWeek dayOfWeek) {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime currentDate = startDate;

        // Find the first occurrence of the target day of week
        while (currentDate.getDayOfWeek() != dayOfWeek && !currentDate.isAfter(endDate)) {
            currentDate = currentDate.plusDays(1);
        }

        // Add all occurrences
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusWeeks(1);
        }

        return dates;
    }

    /**
     * Get all occurrences for a specific day of month in a date range
     */
    public List<LocalDateTime> getOccurrencesByDayOfMonth(LocalDateTime startDate, 
                                                        LocalDateTime endDate, 
                                                        int dayOfMonth) {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime currentDate = startDate;

        // Find the first occurrence of the target day of month
        while (currentDate.getDayOfMonth() != dayOfMonth && !currentDate.isAfter(endDate)) {
            currentDate = currentDate.plusDays(1);
        }

        // Add all occurrences
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusMonths(1);
            
            // Handle month length issues
            int targetDay = Math.min(dayOfMonth, currentDate.toLocalDate().lengthOfMonth());
            currentDate = currentDate.withDayOfMonth(targetDay);
        }

        return dates;
    }

    /**
     * Calculate the duration between consecutive occurrences
     */
    public Duration getIntervalDuration(RecurrencePattern pattern) {
        if (pattern == null) {
            return Duration.ofDays(1);
        }

        RecurrencePattern.RecurrenceType type = pattern.getRecurrenceType();
        int interval = pattern.getInterval();

        switch (type) {
            case DAILY:
                return Duration.ofDays(interval);
            case WEEKLY:
                return Duration.ofDays(interval * 7);
            case MONTHLY:
                return Duration.ofDays(interval * 30); // Approximate
            case YEARLY:
                return Duration.ofDays(interval * 365); // Approximate
            default:
                return Duration.ofDays(1);
        }
    }

    /**
     * Calculate the end date for a recurring task instance (from RecurrenceCalculator)
     */
    public LocalDateTime calculateEndDate(LocalDateTime startDate, LocalDateTime originalStartDate, LocalDateTime originalEndDate) {
        if (originalEndDate == null) {
            return startDate.plusHours(1); // Default 1 hour duration
        }
        
        long durationInMinutes = java.time.Duration.between(originalStartDate, originalEndDate).toMinutes();
        return startDate.plusMinutes(durationInMinutes);
    }

    /**
     * Check if a date falls within the recurrence pattern's active period (from RecurrenceCalculator)
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
     * Find the next occurrence of a specific day of the week from a given date (from RecurrenceCalculator)
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
     * Validate recurrence pattern (from RecurrenceCalculator)
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