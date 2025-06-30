package com.checkproof.explore.ai_tools_java_cursor.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for date range operations and calculations
 * Focused on range-specific operations, calendar boundaries handled by CalendarUtil
 */
@Component
@Slf4j
public class DateRangeUtil {

    // Default business hours: 9 AM to 5 PM
    private static final LocalTime DEFAULT_BUSINESS_START = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_BUSINESS_END = LocalTime.of(17, 0);
    
    // Default timezone
    private static final ZoneId DEFAULT_ZONE = ZoneOffset.UTC;

    /**
     * Check if two date ranges overlap
     */
    public boolean hasOverlap(LocalDateTime start1, LocalDateTime end1, 
                             LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Calculate the overlap duration between two date ranges
     */
    public Duration calculateOverlap(LocalDateTime start1, LocalDateTime end1, 
                                   LocalDateTime start2, LocalDateTime end2) {
        if (!hasOverlap(start1, end1, start2, end2)) {
            return Duration.ZERO;
        }

        LocalDateTime overlapStart = start1.isAfter(start2) ? start1 : start2;
        LocalDateTime overlapEnd = end1.isBefore(end2) ? end1 : end2;
        
        return Duration.between(overlapStart, overlapEnd);
    }

    /**
     * Get the intersection of two date ranges
     */
    public DateRange getIntersection(LocalDateTime start1, LocalDateTime end1, 
                                   LocalDateTime start2, LocalDateTime end2) {
        if (!hasOverlap(start1, end1, start2, end2)) {
            return null;
        }

        LocalDateTime intersectionStart = start1.isAfter(start2) ? start1 : start2;
        LocalDateTime intersectionEnd = end1.isBefore(end2) ? end1 : end2;
        
        return new DateRange(intersectionStart, intersectionEnd);
    }

    /**
     * Calculate business days between two dates (excluding weekends)
     */
    public long calculateBusinessDays(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return 0;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long weekends = 0;

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                weekends++;
            }
            current = current.plusDays(1);
        }

        return days - weekends;
    }

    /**
     * Calculate business days between two dates with custom weekend days
     */
    public long calculateBusinessDays(LocalDate startDate, LocalDate endDate, Set<DayOfWeek> weekendDays) {
        if (startDate.isAfter(endDate)) {
            return 0;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long weekends = 0;

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (weekendDays.contains(current.getDayOfWeek())) {
                weekends++;
            }
            current = current.plusDays(1);
        }

        return days - weekends;
    }

    /**
     * Calculate business hours between two date times
     */
    public Duration calculateBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return calculateBusinessHours(startDateTime, endDateTime, DEFAULT_BUSINESS_START, DEFAULT_BUSINESS_END);
    }

    /**
     * Calculate business hours between two date times with custom business hours
     */
    public Duration calculateBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                         LocalTime businessStart, LocalTime businessEnd) {
        if (startDateTime.isAfter(endDateTime)) {
            return Duration.ZERO;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime current = startDateTime;

        while (current.toLocalDate().isBefore(endDateTime.toLocalDate()) || 
               current.toLocalDate().equals(endDateTime.toLocalDate())) {
            
            LocalDate currentDate = current.toLocalDate();
            LocalTime currentTime = current.toLocalTime();
            
            // Skip weekends
            if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || 
                currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                current = current.plusDays(1).with(businessStart);
                continue;
            }

            LocalDateTime dayStart = LocalDateTime.of(currentDate, businessStart);
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, businessEnd);

            // If it's the first day, use the actual start time
            if (currentDate.equals(startDateTime.toLocalDate())) {
                dayStart = currentTime.isBefore(businessStart) ? dayStart : 
                          currentTime.isAfter(businessEnd) ? dayEnd : LocalDateTime.of(currentDate, currentTime);
            }

            // If it's the last day, use the actual end time
            if (currentDate.equals(endDateTime.toLocalDate())) {
                dayEnd = endDateTime.toLocalTime().isAfter(businessEnd) ? dayEnd : 
                        endDateTime.toLocalTime().isBefore(businessStart) ? dayStart : 
                        LocalDateTime.of(currentDate, endDateTime.toLocalTime());
            }

            if (dayStart.isBefore(dayEnd)) {
                totalDuration = totalDuration.plus(Duration.between(dayStart, dayEnd));
            }

            current = current.plusDays(1).with(businessStart);
        }

        return totalDuration;
    }

    /**
     * Get all dates in a range
     */
    public List<LocalDate> getDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }

        return dates;
    }

    /**
     * Get business dates in a range (excluding weekends)
     */
    public List<LocalDate> getBusinessDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY && 
                current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates.add(current);
            }
            current = current.plusDays(1);
        }

        return dates;
    }

    /**
     * Get the next business day
     */
    public LocalDate getNextBusinessDay(LocalDate date) {
        LocalDate nextDay = date.plusDays(1);
        while (nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || 
               nextDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    /**
     * Get the previous business day
     */
    public LocalDate getPreviousBusinessDay(LocalDate date) {
        LocalDate previousDay = date.minusDays(1);
        while (previousDay.getDayOfWeek() == DayOfWeek.SATURDAY || 
               previousDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            previousDay = previousDay.minusDays(1);
        }
        return previousDay;
    }

    /**
     * Get the start of the week (Monday)
     */
    public LocalDate getStartOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Get the end of the week (Sunday)
     */
    public LocalDate getEndOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * Get the start of the month
     */
    public LocalDate getStartOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * Get the end of the month
     */
    public LocalDate getEndOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * Get the start of the year
     */
    public LocalDate getStartOfYear(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * Get the end of the year
     */
    public LocalDate getEndOfYear(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * Convert LocalDateTime to a different timezone
     */
    public LocalDateTime convertTimeZone(LocalDateTime dateTime, ZoneId fromZone, ZoneId toZone) {
        ZonedDateTime zonedDateTime = dateTime.atZone(fromZone);
        return zonedDateTime.withZoneSameInstant(toZone).toLocalDateTime();
    }

    /**
     * Convert LocalDateTime to UTC
     */
    public LocalDateTime toUtc(LocalDateTime dateTime, ZoneId fromZone) {
        return convertTimeZone(dateTime, fromZone, ZoneOffset.UTC);
    }

    /**
     * Convert UTC LocalDateTime to a specific timezone
     */
    public LocalDateTime fromUtc(LocalDateTime utcDateTime, ZoneId toZone) {
        return convertTimeZone(utcDateTime, ZoneOffset.UTC, toZone);
    }

    /**
     * Check if a date is a holiday (basic implementation - can be extended)
     */
    public boolean isHoliday(LocalDate date) {
        // Basic holiday check - can be extended with actual holiday calendar
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        
        // New Year's Day
        if (month == 1 && day == 1) return true;
        
        // Christmas Day
        if (month == 12 && day == 25) return true;
        
        // Add more holidays as needed
        
        return false;
    }

    /**
     * Calculate business days excluding holidays
     */
    public long calculateBusinessDaysExcludingHolidays(LocalDate startDate, LocalDate endDate) {
        long businessDays = calculateBusinessDays(startDate, endDate);
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (isHoliday(current) && 
                current.getDayOfWeek() != DayOfWeek.SATURDAY && 
                current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                businessDays--;
            }
            current = current.plusDays(1);
        }
        
        return businessDays;
    }

    /**
     * Inner class to represent a date range
     */
    public static class DateRange {
        private final LocalDateTime start;
        private final LocalDateTime end;

        public DateRange(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalDateTime getStart() { return start; }
        public LocalDateTime getEnd() { return end; }
        public Duration getDuration() { return Duration.between(start, end); }
    }
} 