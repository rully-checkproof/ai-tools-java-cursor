package com.checkproof.explore.ai_tools_java_cursor.util;

import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RecurrenceUtil Tests")
class RecurrenceUtilTest {

    private RecurrenceUtil recurrenceUtil;
    private LocalDateTime baseDate;

    @BeforeEach
    void setUp() {
        recurrenceUtil = new RecurrenceUtil();
        baseDate = LocalDateTime.of(2024, 1, 15, 10, 0); // Monday, January 15, 2024
    }

    @Test
    @DisplayName("Should generate daily recurring dates")
    void generateRecurringDates_Daily() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(2)
                .build();

        List<LocalDateTime> dates = recurrenceUtil.generateRecurringDates(baseDate, pattern, 5);

        assertEquals(5, dates.size());
        assertEquals(baseDate, dates.get(0));
        assertEquals(baseDate.plusDays(2), dates.get(1));
        assertEquals(baseDate.plusDays(4), dates.get(2));
        assertEquals(baseDate.plusDays(6), dates.get(3));
        assertEquals(baseDate.plusDays(8), dates.get(4));
    }

    @Test
    @DisplayName("Should generate weekly recurring dates")
    void generateRecurringDates_Weekly() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .build();

        List<LocalDateTime> dates = recurrenceUtil.generateRecurringDates(baseDate, pattern, 3);

        assertEquals(3, dates.size());
        assertEquals(baseDate, dates.get(0));
        assertEquals(baseDate.plusWeeks(1), dates.get(1));
        assertEquals(baseDate.plusWeeks(2), dates.get(2));
    }

    @Test
    @DisplayName("Should generate monthly recurring dates")
    void generateRecurringDates_Monthly() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .build();

        List<LocalDateTime> dates = recurrenceUtil.generateRecurringDates(baseDate, pattern, 3);

        assertEquals(3, dates.size());
        assertEquals(baseDate, dates.get(0));
        assertEquals(baseDate.plusMonths(1), dates.get(1));
        assertEquals(baseDate.plusMonths(2), dates.get(2));
    }

    @Test
    @DisplayName("Should generate yearly recurring dates")
    void generateRecurringDates_Yearly() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.YEARLY)
                .interval(1)
                .build();

        List<LocalDateTime> dates = recurrenceUtil.generateRecurringDates(baseDate, pattern, 3);

        assertEquals(3, dates.size());
        assertEquals(baseDate, dates.get(0));
        assertEquals(baseDate.plusYears(1), dates.get(1));
        assertEquals(baseDate.plusYears(2), dates.get(2));
    }

    @Test
    @DisplayName("Should generate recurring dates within range")
    void generateRecurringDatesInRange() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        LocalDateTime endDate = baseDate.plusDays(5);
        List<LocalDateTime> dates = recurrenceUtil.generateRecurringDatesInRange(baseDate, endDate, pattern);

        assertEquals(6, dates.size());
        assertTrue(dates.stream().allMatch(date -> !date.isAfter(endDate)));
    }

    @Test
    @DisplayName("Should generate recurrence dates with end date")
    void generateRecurrenceDates_WithEndDate() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .endDate(LocalDate.of(2024, 1, 20))
                .build();

        List<LocalDateTime> dates = recurrenceUtil.generateRecurrenceDates(baseDate, pattern, 10);

        assertEquals(5, dates.size()); // 15th to 19th inclusive (20th is excluded as it's end date)
        assertTrue(dates.stream().allMatch(date -> !date.toLocalDate().isAfter(LocalDate.of(2024, 1, 19))));
    }

    @Test
    @DisplayName("Should generate weekly recurrence dates with specific days")
    void generateWeeklyRecurrenceDates_WithSpecificDays() {
        Set<DayOfWeek> daysOfWeek = new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .daysOfWeek(daysOfWeek)
                .build();

        List<LocalDateTime> dates = recurrenceUtil.generateWeeklyRecurrenceDates(baseDate, pattern, 6);

        assertEquals(6, dates.size());
        assertTrue(dates.stream().allMatch(date -> daysOfWeek.contains(date.getDayOfWeek())));
    }

    @Test
    @DisplayName("Should get next daily occurrence")
    void getNextOccurrence_Daily() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(3)
                .build();

        LocalDateTime next = recurrenceUtil.getNextOccurrence(baseDate, pattern);
        assertEquals(baseDate.plusDays(3), next);
    }

    @Test
    @DisplayName("Should get next weekly occurrence")
    void getNextOccurrence_Weekly() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(2)
                .build();

        LocalDateTime next = recurrenceUtil.getNextOccurrence(baseDate, pattern);
        assertEquals(baseDate.plusWeeks(2), next);
    }

    @Test
    @DisplayName("Should get next monthly occurrence")
    void getNextOccurrence_Monthly() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .build();

        LocalDateTime next = recurrenceUtil.getNextOccurrence(baseDate, pattern);
        assertEquals(baseDate.plusMonths(1), next);
    }

    @Test
    @DisplayName("Should get next yearly occurrence")
    void getNextOccurrence_Yearly() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.YEARLY)
                .interval(1)
                .build();

        LocalDateTime next = recurrenceUtil.getNextOccurrence(baseDate, pattern);
        assertEquals(baseDate.plusYears(1), next);
    }

    @Test
    @DisplayName("Should handle null pattern with default behavior")
    void getNextOccurrence_NullPattern() {
        LocalDateTime next = recurrenceUtil.getNextOccurrence(baseDate, null);
        assertEquals(baseDate.plusDays(1), next);
    }

    @Test
    @DisplayName("Should calculate next occurrence")
    void calculateNextOccurrence() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(2)
                .build();

        LocalDateTime next = recurrenceUtil.calculateNextOccurrence(baseDate, pattern);
        assertEquals(baseDate.plusDays(2), next);
    }

    @Test
    @DisplayName("Should throw exception for null pattern in calculateNextOccurrence")
    void calculateNextOccurrence_NullPattern() {
        assertThrows(IllegalArgumentException.class, () -> 
            recurrenceUtil.calculateNextOccurrence(baseDate, null));
    }

    @Test
    @DisplayName("Should get next weekly occurrence with specific day")
    void getNextWeeklyOccurrence_WithSpecificDay() {
        LocalDateTime next = recurrenceUtil.getNextWeeklyOccurrence(baseDate, DayOfWeek.FRIDAY, 1);
        assertEquals(DayOfWeek.FRIDAY, next.getDayOfWeek());
        assertTrue(next.isAfter(baseDate));
    }

    @Test
    @DisplayName("Should get next monthly occurrence with day of month")
    void getNextMonthlyOccurrence_WithDayOfMonth() {
        LocalDateTime next = recurrenceUtil.getNextMonthlyOccurrence(baseDate, 20, 1);
        assertEquals(20, next.getDayOfMonth());
        assertTrue(next.isAfter(baseDate));
    }

    @Test
    @DisplayName("Should handle invalid day of month")
    void getNextMonthlyOccurrence_InvalidDayOfMonth() {
        LocalDateTime dateWith31st = LocalDateTime.of(2024, 1, 31, 10, 0);
        LocalDateTime next = recurrenceUtil.getNextMonthlyOccurrence(dateWith31st, 31, 1);
        // Should handle February which doesn't have 31st
        assertTrue(next.isAfter(dateWith31st));
    }

    @Test
    @DisplayName("Should get next yearly occurrence")
    void getNextYearlyOccurrence_WithMonthAndDay() {
        LocalDateTime next = recurrenceUtil.getNextYearlyOccurrence(baseDate, 6, 15, 1);
        assertEquals(6, next.getMonthValue());
        assertEquals(15, next.getDayOfMonth());
        assertTrue(next.isAfter(baseDate));
    }

    @Test
    @DisplayName("Should calculate occurrences between dates")
    void calculateOccurrences() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        LocalDateTime endDate = baseDate.plusDays(5);
        int count = recurrenceUtil.calculateOccurrences(baseDate, endDate, pattern);
        assertEquals(6, count); // Including start and end dates
    }

    @Test
    @DisplayName("Should return zero occurrences for invalid range")
    void calculateOccurrences_InvalidRange() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        int count = recurrenceUtil.calculateOccurrences(baseDate.plusDays(5), baseDate, pattern);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Should validate occurrence")
    void isValidOccurrence() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        assertTrue(recurrenceUtil.isValidOccurrence(baseDate.plusDays(1), baseDate, pattern));
        assertFalse(recurrenceUtil.isValidOccurrence(baseDate.plusMinutes(30), baseDate, pattern));
    }

    @Test
    @DisplayName("Should get nth occurrence")
    void getNthOccurrence() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        LocalDateTime third = recurrenceUtil.getNthOccurrence(baseDate, pattern, 3);
        assertEquals(baseDate.plusDays(2), third);
    }

    @Test
    @DisplayName("Should throw exception for invalid nth occurrence")
    void getNthOccurrence_InvalidN() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        assertThrows(IllegalArgumentException.class, () -> 
            recurrenceUtil.getNthOccurrence(baseDate, pattern, 0));
    }

    @Test
    @DisplayName("Should get last occurrence before date")
    void getLastOccurrenceBefore() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        LocalDateTime last = recurrenceUtil.getLastOccurrenceBefore(baseDate.plusDays(3), baseDate, pattern);
        assertEquals(baseDate.plusDays(3), last);
    }

    @Test
    @DisplayName("Should return null for date before start")
    void getLastOccurrenceBefore_DateBeforeStart() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        LocalDateTime last = recurrenceUtil.getLastOccurrenceBefore(baseDate.minusDays(1), baseDate, pattern);
        assertNull(last);
    }

    @Test
    @DisplayName("Should get first occurrence after date")
    void getFirstOccurrenceAfter() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();
        LocalDateTime expectedDate = baseDate.plusDays(2);
        LocalDateTime first = recurrenceUtil.getFirstOccurrenceAfter(baseDate.plusDays(1), baseDate, pattern);
        assertEquals(expectedDate, first);
    }

    @Test
    @DisplayName("Should skip occurrences")
    void skipOccurrences() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        LocalDateTime skipped = recurrenceUtil.skipOccurrences(baseDate, pattern, 3);
        assertEquals(baseDate.plusDays(3), skipped);
    }

    @Test
    @DisplayName("Should return same date for zero skip count")
    void skipOccurrences_ZeroSkip() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        LocalDateTime result = recurrenceUtil.skipOccurrences(baseDate, pattern, 0);
        assertEquals(baseDate, result);
    }

    @Test
    @DisplayName("Should get occurrences by day of week")
    void getOccurrencesByDayOfWeek() {
        List<LocalDateTime> occurrences = recurrenceUtil.getOccurrencesByDayOfWeek(
            baseDate, baseDate.plusWeeks(2), DayOfWeek.MONDAY);

        assertEquals(3, occurrences.size());
        assertTrue(occurrences.stream().allMatch(date -> date.getDayOfWeek() == DayOfWeek.MONDAY));
    }

    @Test
    @DisplayName("Should get occurrences by day of month")
    void getOccurrencesByDayOfMonth() {
        List<LocalDateTime> occurrences = recurrenceUtil.getOccurrencesByDayOfMonth(
            baseDate, baseDate.plusMonths(2), 15);

        assertEquals(3, occurrences.size());
        assertTrue(occurrences.stream().allMatch(date -> date.getDayOfMonth() == 15));
    }

    @Test
    @DisplayName("Should calculate interval duration")
    void getIntervalDuration() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(2)
                .build();

        Duration duration = recurrenceUtil.getIntervalDuration(pattern);
        assertEquals(Duration.ofDays(2), duration);
    }

    @Test
    @DisplayName("Should return default duration for null pattern")
    void getIntervalDuration_NullPattern() {
        Duration duration = recurrenceUtil.getIntervalDuration(null);
        assertEquals(Duration.ofDays(1), duration);
    }

    @Test
    @DisplayName("Should calculate end date")
    void calculateEndDate() {
        LocalDateTime originalStart = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime originalEnd = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime newStart = LocalDateTime.of(2024, 1, 20, 14, 0);

        LocalDateTime newEnd = recurrenceUtil.calculateEndDate(newStart, originalStart, originalEnd);
        assertEquals(newStart.plusHours(2), newEnd);
    }

    @Test
    @DisplayName("Should calculate end date with null original end")
    void calculateEndDate_NullOriginalEnd() {
        LocalDateTime originalStart = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime newStart = LocalDateTime.of(2024, 1, 20, 14, 0);

        LocalDateTime newEnd = recurrenceUtil.calculateEndDate(newStart, originalStart, null);
        assertEquals(newStart.plusHours(1), newEnd);
    }

    @Test
    @DisplayName("Should check if date is in active period")
    void isDateInActivePeriod() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();

        assertTrue(recurrenceUtil.isDateInActivePeriod(baseDate, pattern));
        assertFalse(recurrenceUtil.isDateInActivePeriod(
            LocalDateTime.of(2023, 12, 31, 10, 0), pattern));
    }

    @Test
    @DisplayName("Should validate recurrence pattern")
    void validateRecurrencePattern() {
        RecurrencePattern validPattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        assertDoesNotThrow(() -> recurrenceUtil.validateRecurrencePattern(validPattern));
    }

    @Test
    @DisplayName("Should throw exception for null pattern")
    void validateRecurrencePattern_NullPattern() {
        assertThrows(IllegalArgumentException.class, () -> 
            recurrenceUtil.validateRecurrencePattern(null));
    }

    @Test
    @DisplayName("Should throw exception for null recurrence type")
    void validateRecurrencePattern_NullRecurrenceType() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .interval(1)
                .build();

        assertThrows(IllegalArgumentException.class, () -> 
            recurrenceUtil.validateRecurrencePattern(pattern));
    }

    @Test
    @DisplayName("Should throw exception for invalid interval")
    void validateRecurrencePattern_InvalidInterval() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> 
            recurrenceUtil.validateRecurrencePattern(pattern));
    }

    @Test
    @DisplayName("Should throw exception for invalid date range")
    void validateRecurrencePattern_InvalidDateRange() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .startDate(LocalDate.of(2024, 12, 31))
                .endDate(LocalDate.of(2024, 1, 1))
                .build();

        assertThrows(IllegalArgumentException.class, () -> 
            recurrenceUtil.validateRecurrencePattern(pattern));
    }

    @Test
    @DisplayName("Should throw exception for invalid max occurrences")
    void validateRecurrencePattern_InvalidMaxOccurrences() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .maxOccurrences(0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> 
            recurrenceUtil.validateRecurrencePattern(pattern));
    }
} 