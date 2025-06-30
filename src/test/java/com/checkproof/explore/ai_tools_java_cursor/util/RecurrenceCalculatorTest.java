package com.checkproof.explore.ai_tools_java_cursor.util;

import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecurrenceUtil Tests")
class RecurrenceUtilTest {

    @InjectMocks
    private RecurrenceUtil recurrenceUtil;

    private LocalDateTime baseDate;
    private RecurrencePattern dailyPattern;
    private RecurrencePattern weeklyPattern;
    private RecurrencePattern monthlyPattern;
    private RecurrencePattern yearlyPattern;

    @BeforeEach
    void setUp() {
        baseDate = LocalDateTime.of(2024, 1, 15, 10, 0); // Monday, January 15, 2024

        dailyPattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        weeklyPattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .daysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
                .build();

        monthlyPattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .dayOfMonth(15)
                .build();

        yearlyPattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.YEARLY)
                .interval(1)
                .build();
    }

    @Test
    @DisplayName("Should calculate next daily occurrence")
    void shouldCalculateNextDailyOccurrence() {
        LocalDateTime result = recurrenceUtil.calculateNextOccurrence(baseDate, dailyPattern);
        assertEquals(baseDate.plusDays(1), result);
    }

    @Test
    @DisplayName("Should calculate next weekly occurrence")
    void shouldCalculateNextWeeklyOccurrence() {
        LocalDateTime result = recurrenceUtil.calculateNextOccurrence(baseDate, weeklyPattern);
        assertEquals(baseDate.plusDays(2), result); // Wednesday
    }

    @Test
    @DisplayName("Should calculate next monthly occurrence")
    void shouldCalculateNextMonthlyOccurrence() {
        LocalDateTime result = recurrenceUtil.calculateNextOccurrence(baseDate, monthlyPattern);
        assertEquals(LocalDateTime.of(2024, 2, 15, 10, 0), result);
    }

    @Test
    @DisplayName("Should calculate next yearly occurrence")
    void shouldCalculateNextYearlyOccurrence() {
        LocalDateTime result = recurrenceUtil.calculateNextOccurrence(baseDate, yearlyPattern);
        assertEquals(LocalDateTime.of(2025, 1, 15, 10, 0), result);
    }

    @Test
    @DisplayName("Should throw exception for null pattern")
    void shouldThrowExceptionForNullPattern() {
        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.calculateNextOccurrence(baseDate, null);
        });
    }

    @Test
    @DisplayName("Should throw exception for null recurrence type")
    void shouldThrowExceptionForNullRecurrenceType() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .interval(1)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.calculateNextOccurrence(baseDate, pattern);
        });
    }

    @Test
    @DisplayName("Should handle weekly pattern without days of week")
    void shouldHandleWeeklyPatternWithoutDaysOfWeek() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .build();

        LocalDateTime result = recurrenceUtil.calculateNextOccurrence(baseDate, pattern);
        assertEquals(baseDate.plusWeeks(1), result);
    }

    @Test
    @DisplayName("Should calculate next occurrence with days of week")
    void shouldCalculateNextOccurrenceWithDaysOfWeek() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .daysOfWeek(Set.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY))
                .build();

        LocalDateTime result = recurrenceUtil.calculateNextOccurrenceWithDaysOfWeek(baseDate, pattern);
        assertEquals(baseDate.plusDays(1), result); // Tuesday
    }

    @Test
    @DisplayName("Should handle pattern without days of week")
    void shouldHandlePatternWithoutDaysOfWeek() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        LocalDateTime result = recurrenceUtil.calculateNextOccurrenceWithDaysOfWeek(baseDate, pattern);
        assertEquals(baseDate.plusDays(1), result);
    }

    @Test
    @DisplayName("Should calculate next monthly occurrence with day of month")
    void shouldCalculateNextMonthlyOccurrenceWithDayOfMonth() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .dayOfMonth(20)
                .build();

        LocalDateTime result = recurrenceUtil.getNextMonthlyOccurrence(baseDate, pattern);
        assertEquals(LocalDateTime.of(2024, 2, 20, 10, 0), result);
    }

    @Test
    @DisplayName("Should handle invalid day of month")
    void shouldHandleInvalidDayOfMonth() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .dayOfMonth(31)
                .build();

        LocalDateTime febDate = LocalDateTime.of(2024, 1, 31, 10, 0);
        LocalDateTime result = recurrenceUtil.getNextMonthlyOccurrence(febDate, pattern);
        // Should use last day of February (29 in 2024 - leap year)
        assertEquals(LocalDateTime.of(2024, 2, 29, 10, 0), result);
    }

    @Test
    @DisplayName("Should calculate next monthly occurrence with week of month")
    void shouldCalculateNextMonthlyOccurrenceWithWeekOfMonth() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .weekOfMonth(2)
                .daysOfWeek(Set.of(DayOfWeek.MONDAY))
                .build();

        LocalDateTime result = recurrenceUtil.getNextMonthlyOccurrence(baseDate, pattern);
        // Second Monday of February 2024
        assertEquals(LocalDateTime.of(2024, 2, 12, 10, 0), result);
    }

    @Test
    @DisplayName("Should handle monthly pattern without specific day or week")
    void shouldHandleMonthlyPatternWithoutSpecificDayOrWeek() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .build();

        LocalDateTime result = recurrenceUtil.getNextMonthlyOccurrence(baseDate, pattern);
        assertEquals(baseDate.plusMonths(1), result);
    }

    @Test
    @DisplayName("Should throw exception for invalid monthly pattern")
    void shouldThrowExceptionForInvalidMonthlyPattern() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .weekOfMonth(2)
                // Missing daysOfWeek
                .build();

        assertThrows(UnsupportedOperationException.class, () -> {
            recurrenceUtil.getNextMonthlyOccurrence(baseDate, pattern);
        });
    }

    @Test
    @DisplayName("Should calculate end date for recurring task")
    void shouldCalculateEndDateForRecurringTask() {
        LocalDateTime originalStart = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime originalEnd = LocalDateTime.of(2024, 1, 15, 11, 30);
        LocalDateTime newStart = LocalDateTime.of(2024, 1, 16, 10, 0);

        LocalDateTime result = recurrenceUtil.calculateEndDate(newStart, originalStart, originalEnd);
        assertEquals(LocalDateTime.of(2024, 1, 16, 11, 30), result);
    }

    @Test
    @DisplayName("Should calculate end date with null original end")
    void shouldCalculateEndDateWithNullOriginalEnd() {
        LocalDateTime originalStart = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime newStart = LocalDateTime.of(2024, 1, 16, 10, 0);

        LocalDateTime result = recurrenceUtil.calculateEndDate(newStart, originalStart, null);
        assertEquals(LocalDateTime.of(2024, 1, 16, 11, 0), result); // Default 1 hour
    }

    @Test
    @DisplayName("Should check if date is in active period")
    void shouldCheckIfDateIsInActivePeriod() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();

        boolean result = recurrenceUtil.isDateInActivePeriod(baseDate, pattern);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for date before start date")
    void shouldReturnFalseForDateBeforeStartDate() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .startDate(LocalDate.of(2024, 2, 1))
                .build();

        boolean result = recurrenceUtil.isDateInActivePeriod(baseDate, pattern);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for date after end date")
    void shouldReturnFalseForDateAfterEndDate() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .endDate(LocalDate.of(2024, 1, 1))
                .build();

        boolean result = recurrenceUtil.isDateInActivePeriod(baseDate, pattern);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true for date without start/end constraints")
    void shouldReturnTrueForDateWithoutStartEndConstraints() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .build();

        boolean result = recurrenceUtil.isDateInActivePeriod(baseDate, pattern);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should generate recurrence dates")
    void shouldGenerateRecurrenceDates() {
        List<LocalDateTime> result = recurrenceUtil.generateRecurrenceDates(baseDate, dailyPattern, 10);
        
        assertEquals(10, result.size());
        assertEquals(baseDate, result.get(0));
        assertEquals(baseDate.plusDays(1), result.get(1));
        assertEquals(baseDate.plusDays(9), result.get(9));
    }

    @Test
    @DisplayName("Should respect max occurrences limit")
    void shouldRespectMaxOccurrencesLimit() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .maxOccurrences(5)
                .build();

        List<LocalDateTime> result = recurrenceUtil.generateRecurrenceDates(baseDate, pattern, 10);
        assertEquals(5, result.size());
    }

    @Test
    @DisplayName("Should respect end date limit")
    void shouldRespectEndDateLimit() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .endDate(LocalDate.of(2024, 1, 17))
                .build();

        List<LocalDateTime> result = recurrenceUtil.generateRecurrenceDates(baseDate, pattern, 10);
        assertEquals(3, result.size()); // 15th, 16th, 17th
    }

    @Test
    @DisplayName("Should handle pattern with both end date and max occurrences")
    void shouldHandlePatternWithBothEndDateAndMaxOccurrences() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .endDate(LocalDate.of(2024, 1, 20))
                .maxOccurrences(3)
                .build();

        List<LocalDateTime> result = recurrenceUtil.generateRecurrenceDates(baseDate, pattern, 10);
        assertEquals(3, result.size()); // Should respect max occurrences
    }

    @Test
    @DisplayName("Should generate weekly recurrence dates with multiple days")
    void shouldGenerateWeeklyRecurrenceDatesWithMultipleDays() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .daysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
                .build();

        List<LocalDateTime> result = recurrenceUtil.generateWeeklyRecurrenceDates(baseDate, pattern, 10);
        
        assertTrue(result.size() > 0);
        // All dates should be Monday, Wednesday, or Friday
        for (LocalDateTime date : result) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            assertTrue(dayOfWeek == DayOfWeek.MONDAY || 
                      dayOfWeek == DayOfWeek.WEDNESDAY || 
                      dayOfWeek == DayOfWeek.FRIDAY);
        }
    }

    @Test
    @DisplayName("Should handle weekly pattern without days of week in generation")
    void shouldHandleWeeklyPatternWithoutDaysOfWeekInGeneration() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .build();

        List<LocalDateTime> result = recurrenceUtil.generateWeeklyRecurrenceDates(baseDate, pattern, 10);
        assertEquals(10, result.size());
    }

    @Test
    @DisplayName("Should respect max occurrences in weekly pattern")
    void shouldRespectMaxOccurrencesInWeeklyPattern() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .daysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
                .maxOccurrences(5)
                .build();

        List<LocalDateTime> result = recurrenceUtil.generateWeeklyRecurrenceDates(baseDate, pattern, 10);
        assertEquals(5, result.size());
    }

    @Test
    @DisplayName("Should validate valid recurrence pattern")
    void shouldValidateValidRecurrencePattern() {
        assertDoesNotThrow(() -> {
            recurrenceUtil.validateRecurrencePattern(dailyPattern);
        });
    }

    @Test
    @DisplayName("Should throw exception for null pattern in validation")
    void shouldThrowExceptionForNullPatternInValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.validateRecurrencePattern(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for null recurrence type in validation")
    void shouldThrowExceptionForNullRecurrenceTypeInValidation() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .interval(1)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.validateRecurrencePattern(pattern);
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid interval")
    void shouldThrowExceptionForInvalidInterval() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.validateRecurrencePattern(pattern);
        });
    }

    @Test
    @DisplayName("Should throw exception for end date before start date")
    void shouldThrowExceptionForEndDateBeforeStartDate() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .startDate(LocalDate.of(2024, 2, 1))
                .endDate(LocalDate.of(2024, 1, 1))
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.validateRecurrencePattern(pattern);
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid max occurrences")
    void shouldThrowExceptionForInvalidMaxOccurrences() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.DAILY)
                .interval(1)
                .maxOccurrences(0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.validateRecurrencePattern(pattern);
        });
    }

    @Test
    @DisplayName("Should handle complex monthly pattern with week and day")
    void shouldHandleComplexMonthlyPatternWithWeekAndDay() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .weekOfMonth(3)
                .daysOfWeek(Set.of(DayOfWeek.TUESDAY))
                .build();

        LocalDateTime janDate = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime result = recurrenceUtil.getNextMonthlyOccurrence(janDate, pattern);
        // Third Tuesday of February 2024
        assertEquals(LocalDateTime.of(2024, 2, 20, 10, 0), result);
    }

    @Test
    @DisplayName("Should handle edge case for month end")
    void shouldHandleEdgeCaseForMonthEnd() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.MONTHLY)
                .interval(1)
                .weekOfMonth(5)
                .daysOfWeek(Set.of(DayOfWeek.MONDAY))
                .build();

        LocalDateTime janDate = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime result = recurrenceUtil.getNextMonthlyOccurrence(janDate, pattern);
        // Should handle case where 5th week doesn't exist in the month
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle year boundary")
    void shouldHandleYearBoundary() {
        LocalDateTime yearEndDate = LocalDateTime.of(2023, 12, 31, 10, 0);
        LocalDateTime result = recurrenceUtil.calculateNextOccurrence(yearEndDate, dailyPattern);
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), result);
    }

    @Test
    @DisplayName("Should handle month boundary")
    void shouldHandleMonthBoundary() {
        LocalDateTime monthEndDate = LocalDateTime.of(2024, 1, 31, 10, 0);
        LocalDateTime result = recurrenceUtil.calculateNextOccurrence(monthEndDate, dailyPattern);
        assertEquals(LocalDateTime.of(2024, 2, 1, 10, 0), result);
    }

    @Test
    @DisplayName("Should throw exception for null pattern in calculation")
    void shouldThrowExceptionForNullPatternInCalculation() {
        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.calculateNextOccurrence(baseDate, null);
        });
    }

    @Test
    @DisplayName("Should throw exception for null recurrence type in calculation")
    void shouldThrowExceptionForNullRecurrenceTypeInCalculation() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .interval(1)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            recurrenceUtil.calculateNextOccurrence(baseDate, pattern);
        });
    }

    @Test
    @DisplayName("Should handle weekly pattern without days of week in calculation")
    void shouldHandleWeeklyPatternWithoutDaysOfWeekInCalculation() {
        RecurrencePattern pattern = RecurrencePattern.builder()
                .recurrenceType(RecurrencePattern.RecurrenceType.WEEKLY)
                .interval(1)
                .build();

        LocalDateTime result = recurrenceUtil.calculateNextOccurrence(baseDate, pattern);
        assertEquals(baseDate.plusWeeks(1), result);
    }
}
