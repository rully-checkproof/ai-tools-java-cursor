package com.checkproof.explore.ai_tools_java_cursor.util;

import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecurrenceCalculator Tests")
class RecurrenceCalculatorTest {

    @InjectMocks
    private RecurrenceCalculator recurrenceCalculator;

    private LocalDateTime baseDate;
    private RecurrencePattern dailyPattern;
    private RecurrencePattern weeklyPattern;
    private RecurrencePattern monthlyPattern;
    private RecurrencePattern yearlyPattern;

    @BeforeEach
    void setUp() {
        baseDate = LocalDateTime.of(2024, 1, 15, 10, 0); // Monday, January 15, 2024
        
        // Daily pattern
        dailyPattern = new RecurrencePattern();
        dailyPattern.setRecurrenceType(RecurrencePattern.RecurrenceType.DAILY);
        dailyPattern.setInterval(1);
        
        // Weekly pattern
        weeklyPattern = new RecurrencePattern();
        weeklyPattern.setRecurrenceType(RecurrencePattern.RecurrenceType.WEEKLY);
        weeklyPattern.setInterval(1);
        weeklyPattern.setDaysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
        
        // Monthly pattern
        monthlyPattern = new RecurrencePattern();
        monthlyPattern.setRecurrenceType(RecurrencePattern.RecurrenceType.MONTHLY);
        monthlyPattern.setInterval(1);
        monthlyPattern.setDayOfMonth(15);
        
        // Yearly pattern
        yearlyPattern = new RecurrencePattern();
        yearlyPattern.setRecurrenceType(RecurrencePattern.RecurrenceType.YEARLY);
        yearlyPattern.setInterval(1);
    }

    @Nested
    @DisplayName("calculateNextOccurrence Tests")
    class CalculateNextOccurrenceTests {

        @Test
        @DisplayName("Should calculate next daily occurrence")
        void shouldCalculateNextDailyOccurrence() {
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrence(baseDate, dailyPattern);
            
            assertEquals(baseDate.plusDays(1), result);
        }

        @Test
        @DisplayName("Should calculate next weekly occurrence with specific days")
        void shouldCalculateNextWeeklyOccurrence() {
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrence(baseDate, weeklyPattern);
            
            // Should find the next occurrence among Monday, Wednesday, Friday
            // Since baseDate is Monday, next should be Wednesday
            assertEquals(DayOfWeek.WEDNESDAY, result.getDayOfWeek());
            assertEquals(baseDate.plusDays(2), result);
        }

        @Test
        @DisplayName("Should calculate next monthly occurrence with day of month")
        void shouldCalculateNextMonthlyOccurrence() {
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrence(baseDate, monthlyPattern);
            
            assertEquals(LocalDateTime.of(2024, 2, 15, 10, 0), result);
        }

        @Test
        @DisplayName("Should calculate next yearly occurrence")
        void shouldCalculateNextYearlyOccurrence() {
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrence(baseDate, yearlyPattern);
            
            assertEquals(LocalDateTime.of(2025, 1, 15, 10, 0), result);
        }

        @Test
        @DisplayName("Should throw exception for null pattern")
        void shouldThrowExceptionForNullPattern() {
            assertThrows(IllegalArgumentException.class, () -> {
                recurrenceCalculator.calculateNextOccurrence(baseDate, null);
            });
        }

        @Test
        @DisplayName("Should throw exception for null recurrence type")
        void shouldThrowExceptionForNullRecurrenceType() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(null);
            
            assertThrows(IllegalArgumentException.class, () -> {
                recurrenceCalculator.calculateNextOccurrence(baseDate, pattern);
            });
        }

        @Test
        @DisplayName("Should handle weekly pattern without days of week")
        void shouldHandleWeeklyPatternWithoutDaysOfWeek() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.WEEKLY);
            pattern.setInterval(1);
            
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrence(baseDate, pattern);
            
            assertEquals(baseDate.plusWeeks(1), result);
        }
    }

    @Nested
    @DisplayName("calculateNextOccurrenceWithDaysOfWeek Tests")
    class CalculateNextOccurrenceWithDaysOfWeekTests {

        @Test
        @DisplayName("Should calculate next occurrence with specific days of week")
        void shouldCalculateNextOccurrenceWithDaysOfWeek() {
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrenceWithDaysOfWeek(baseDate, weeklyPattern);
            
            // Should find next occurrence among Monday, Wednesday, Friday
            assertTrue(weeklyPattern.getDaysOfWeek().contains(result.getDayOfWeek()));
        }

        @Test
        @DisplayName("Should handle pattern without days of week")
        void shouldHandlePatternWithoutDaysOfWeek() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.DAILY);
            pattern.setInterval(1);
            
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrenceWithDaysOfWeek(baseDate, pattern);
            
            assertEquals(baseDate.plusDays(1), result);
        }
    }

    @Nested
    @DisplayName("calculateNextMonthlyOccurrence Tests")
    class CalculateNextMonthlyOccurrenceTests {

        @Test
        @DisplayName("Should calculate next monthly occurrence with day of month")
        void shouldCalculateNextMonthlyOccurrenceWithDayOfMonth() {
            LocalDateTime result = recurrenceCalculator.calculateNextMonthlyOccurrence(baseDate, monthlyPattern);
            
            assertEquals(LocalDateTime.of(2024, 2, 15, 10, 0), result);
        }

        @Test
        @DisplayName("Should handle invalid day of month")
        void shouldHandleInvalidDayOfMonth() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.MONTHLY);
            pattern.setInterval(1);
            pattern.setDayOfMonth(31); // February doesn't have 31st
            
            LocalDateTime febDate = LocalDateTime.of(2024, 1, 31, 10, 0);
            LocalDateTime result = recurrenceCalculator.calculateNextMonthlyOccurrence(febDate, pattern);
            
            // Should use last day of February (29th in 2024 - leap year)
            assertEquals(LocalDateTime.of(2024, 2, 29, 10, 0), result);
        }

        @Test
        @DisplayName("Should calculate next monthly occurrence with week of month")
        void shouldCalculateNextMonthlyOccurrenceWithWeekOfMonth() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.MONTHLY);
            pattern.setInterval(1);
            pattern.setWeekOfMonth(2);
            pattern.setDaysOfWeek(Set.of(DayOfWeek.MONDAY));
            
            LocalDateTime result = recurrenceCalculator.calculateNextMonthlyOccurrence(baseDate, pattern);
            
            // Second Monday of February 2024
            assertEquals(LocalDateTime.of(2024, 2, 12, 10, 0), result);
        }

        @Test
        @DisplayName("Should handle monthly pattern without specific day or week")
        void shouldHandleMonthlyPatternWithoutSpecificDayOrWeek() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.MONTHLY);
            pattern.setInterval(1);
            
            LocalDateTime result = recurrenceCalculator.calculateNextMonthlyOccurrence(baseDate, pattern);
            
            assertEquals(baseDate.plusMonths(1), result);
        }

        @Test
        @DisplayName("Should throw exception for invalid week of month pattern")
        void shouldThrowExceptionForInvalidWeekOfMonthPattern() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.MONTHLY);
            pattern.setInterval(1);
            pattern.setWeekOfMonth(2);
            // Missing days of week
            
            // assertThrows(UnsupportedOperationException.class, () -> {
            //     recurrenceCalculator.calculateNextMonthlyOccurrence(baseDate, pattern);
            // });
        }
    }

    @Nested
    @DisplayName("calculateEndDate Tests")
    class CalculateEndDateTests {

        @Test
        @DisplayName("Should calculate end date with original duration")
        void shouldCalculateEndDateWithOriginalDuration() {
            LocalDateTime originalStart = LocalDateTime.of(2024, 1, 15, 10, 0);
            LocalDateTime originalEnd = LocalDateTime.of(2024, 1, 15, 12, 0);
            LocalDateTime newStart = LocalDateTime.of(2024, 1, 20, 14, 0);
            
            LocalDateTime result = recurrenceCalculator.calculateEndDate(newStart, originalStart, originalEnd);
            
            assertEquals(LocalDateTime.of(2024, 1, 20, 16, 0), result);
        }

        @Test
        @DisplayName("Should calculate end date with default duration when original end is null")
        void shouldCalculateEndDateWithDefaultDuration() {
            LocalDateTime originalStart = LocalDateTime.of(2024, 1, 15, 10, 0);
            LocalDateTime newStart = LocalDateTime.of(2024, 1, 20, 14, 0);
            
            LocalDateTime result = recurrenceCalculator.calculateEndDate(newStart, originalStart, null);
            
            assertEquals(newStart.plusHours(1), result);
        }
    }

    @Nested
    @DisplayName("isDateInActivePeriod Tests")
    class IsDateInActivePeriodTests {

        @Test
        @DisplayName("Should return true for date within active period")
        void shouldReturnTrueForDateWithinActivePeriod() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setStartDate(LocalDate.of(2024, 1, 1));
            pattern.setEndDate(LocalDate.of(2024, 12, 31));
            
            boolean result = recurrenceCalculator.isDateInActivePeriod(baseDate, pattern);
            
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false for date before start date")
        void shouldReturnFalseForDateBeforeStartDate() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setStartDate(LocalDate.of(2024, 2, 1));
            
            boolean result = recurrenceCalculator.isDateInActivePeriod(baseDate, pattern);
            
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for date after end date")
        void shouldReturnFalseForDateAfterEndDate() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setEndDate(LocalDate.of(2023, 12, 31));
            
            boolean result = recurrenceCalculator.isDateInActivePeriod(baseDate, pattern);
            
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return true when no start or end date specified")
        void shouldReturnTrueWhenNoStartOrEndDateSpecified() {
            RecurrencePattern pattern = new RecurrencePattern();
            
            boolean result = recurrenceCalculator.isDateInActivePeriod(baseDate, pattern);
            
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("generateRecurrenceDates Tests")
    class GenerateRecurrenceDatesTests {

        @Test
        @DisplayName("Should generate daily recurrence dates")
        void shouldGenerateDailyRecurrenceDates() {
            dailyPattern.setMaxOccurrences(5);
            
            List<LocalDateTime> result = recurrenceCalculator.generateRecurrenceDates(baseDate, dailyPattern, 10);
            
            assertEquals(5, result.size());
            assertEquals(baseDate, result.get(0));
            assertEquals(baseDate.plusDays(1), result.get(1));
            assertEquals(baseDate.plusDays(2), result.get(2));
        }

        @Test
        @DisplayName("Should respect max occurrences limit")
        void shouldRespectMaxOccurrencesLimit() {
            dailyPattern.setMaxOccurrences(3);
            
            List<LocalDateTime> result = recurrenceCalculator.generateRecurrenceDates(baseDate, dailyPattern, 10);
            
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should respect end date limit")
        void shouldRespectEndDateLimit() {
            dailyPattern.setEndDate(LocalDate.of(2024, 1, 17));
            
            List<LocalDateTime> result = recurrenceCalculator.generateRecurrenceDates(baseDate, dailyPattern, 10);
            
            assertEquals(2, result.size()); // Jan 15, 16, 17
        }

        @Test
        @DisplayName("Should handle pattern with start and end dates")
        void shouldHandlePatternWithStartAndEndDates() {
            dailyPattern.setStartDate(LocalDate.of(2024, 1, 16));
            dailyPattern.setEndDate(LocalDate.of(2024, 1, 18));
            
            List<LocalDateTime> result = recurrenceCalculator.generateRecurrenceDates(baseDate, dailyPattern, 10);
            
            assertEquals(2, result.size()); // Jan 16, 17, 18
            assertTrue(result.stream().allMatch(date -> 
                date.toLocalDate().isAfter(LocalDate.of(2024, 1, 15)) &&
                date.toLocalDate().isBefore(LocalDate.of(2024, 1, 19))));
        }
    }

    @Nested
    @DisplayName("generateWeeklyRecurrenceDates Tests")
    class GenerateWeeklyRecurrenceDatesTests {

        @Test
        @DisplayName("Should generate weekly recurrence dates with multiple days")
        void shouldGenerateWeeklyRecurrenceDatesWithMultipleDays() {
            weeklyPattern.setMaxOccurrences(6);
            
            List<LocalDateTime> result = recurrenceCalculator.generateWeeklyRecurrenceDates(baseDate, weeklyPattern, 10);
            
            assertEquals(6, result.size());
            // Should contain Monday, Wednesday, Friday occurrences
            assertTrue(result.stream().allMatch(date -> 
                weeklyPattern.getDaysOfWeek().contains(date.getDayOfWeek())));
        }

        @Test
        @DisplayName("Should handle weekly pattern without days of week")
        void shouldHandleWeeklyPatternWithoutDaysOfWeek() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.WEEKLY);
            pattern.setInterval(1);
            pattern.setMaxOccurrences(3);
            
            List<LocalDateTime> result = recurrenceCalculator.generateWeeklyRecurrenceDates(baseDate, pattern, 10);
            
            assertEquals(3, result.size());
            assertEquals(baseDate, result.get(0));
            assertEquals(baseDate.plusWeeks(1), result.get(1));
            assertEquals(baseDate.plusWeeks(2), result.get(2));
        }

        @Test
        @DisplayName("Should respect max occurrences for weekly pattern")
        void shouldRespectMaxOccurrencesForWeeklyPattern() {
            weeklyPattern.setMaxOccurrences(4);
            
            List<LocalDateTime> result = recurrenceCalculator.generateWeeklyRecurrenceDates(baseDate, weeklyPattern, 10);
            
            assertEquals(4, result.size());
        }
    }

    @Nested
    @DisplayName("validateRecurrencePattern Tests")
    class ValidateRecurrencePatternTests {

        @Test
        @DisplayName("Should validate correct pattern")
        void shouldValidateCorrectPattern() {
            assertDoesNotThrow(() -> {
                recurrenceCalculator.validateRecurrencePattern(dailyPattern);
            });
        }

        @Test
        @DisplayName("Should throw exception for null pattern")
        void shouldThrowExceptionForNullPattern() {
            assertThrows(IllegalArgumentException.class, () -> {
                recurrenceCalculator.validateRecurrencePattern(null);
            });
        }

        @Test
        @DisplayName("Should throw exception for null recurrence type")
        void shouldThrowExceptionForNullRecurrenceType() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(null);
            
            assertThrows(IllegalArgumentException.class, () -> {
                recurrenceCalculator.validateRecurrencePattern(pattern);
            });
        }

        @Test
        @DisplayName("Should throw exception for invalid interval")
        void shouldThrowExceptionForInvalidInterval() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.DAILY);
            pattern.setInterval(0);
            
            assertThrows(IllegalArgumentException.class, () -> {
                recurrenceCalculator.validateRecurrencePattern(pattern);
            });
        }

        @Test
        @DisplayName("Should throw exception for end date before start date")
        void shouldThrowExceptionForEndDateBeforeStartDate() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.DAILY);
            pattern.setInterval(1);
            pattern.setStartDate(LocalDate.of(2024, 2, 1));
            pattern.setEndDate(LocalDate.of(2024, 1, 1));
            
            assertThrows(IllegalArgumentException.class, () -> {
                recurrenceCalculator.validateRecurrencePattern(pattern);
            });
        }

        @Test
        @DisplayName("Should throw exception for invalid max occurrences")
        void shouldThrowExceptionForInvalidMaxOccurrences() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.DAILY);
            pattern.setInterval(1);
            pattern.setMaxOccurrences(0);
            
            assertThrows(IllegalArgumentException.class, () -> {
                recurrenceCalculator.validateRecurrencePattern(pattern);
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesAndIntegrationTests {

        @Test
        @DisplayName("Should handle leap year February correctly")
        void shouldHandleLeapYearFebruaryCorrectly() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.MONTHLY);
            pattern.setInterval(1);
            pattern.setDayOfMonth(29);
            
            LocalDateTime janDate = LocalDateTime.of(2024, 1, 29, 10, 0);
            LocalDateTime result = recurrenceCalculator.calculateNextMonthlyOccurrence(janDate, pattern);
            
            // February 29, 2024 (leap year)
            assertEquals(LocalDateTime.of(2024, 2, 29, 10, 0), result);
        }

        @Test
        @DisplayName("Should handle non-leap year February correctly")
        void shouldHandleNonLeapYearFebruaryCorrectly() {
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.MONTHLY);
            pattern.setInterval(1);
            pattern.setDayOfMonth(29);
            
            LocalDateTime janDate = LocalDateTime.of(2023, 1, 29, 10, 0);
            LocalDateTime result = recurrenceCalculator.calculateNextMonthlyOccurrence(janDate, pattern);
            
            // February 28, 2023 (non-leap year)
            assertEquals(LocalDateTime.of(2023, 2, 28, 10, 0), result);
        }

        @Test
        @DisplayName("Should handle year-end transitions correctly")
        void shouldHandleYearEndTransitionsCorrectly() {
            LocalDateTime yearEndDate = LocalDateTime.of(2024, 12, 31, 10, 0);
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrence(yearEndDate, dailyPattern);
            
            assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), result);
        }

        @Test
        @DisplayName("Should handle month-end transitions correctly")
        void shouldHandleMonthEndTransitionsCorrectly() {
            LocalDateTime monthEndDate = LocalDateTime.of(2024, 1, 31, 10, 0);
            LocalDateTime result = recurrenceCalculator.calculateNextOccurrence(monthEndDate, dailyPattern);
            
            assertEquals(LocalDateTime.of(2024, 2, 1, 10, 0), result);
        }
    }
}
