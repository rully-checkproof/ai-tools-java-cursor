package com.checkproof.explore.ai_tools_java_cursor.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DateRangeUtil Tests")
class DateRangeUtilTest {

    private DateRangeUtil dateRangeUtil;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        dateRangeUtil = new DateRangeUtil();
        startDate = LocalDateTime.of(2024, 1, 15, 10, 0); // Monday, January 15, 2024
        endDate = LocalDateTime.of(2024, 1, 15, 12, 0);
    }

    @Test
    @DisplayName("Should detect overlap between date ranges")
    void hasOverlap_WithOverlap() {
        LocalDateTime start1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 1, 15, 11, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 1, 15, 13, 0);

        assertTrue(dateRangeUtil.hasOverlap(start1, end1, start2, end2));
    }

    @Test
    @DisplayName("Should not detect overlap between non-overlapping ranges")
    void hasOverlap_NoOverlap() {
        LocalDateTime start1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 1, 15, 13, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 1, 15, 15, 0);

        assertFalse(dateRangeUtil.hasOverlap(start1, end1, start2, end2));
    }

    @Test
    @DisplayName("Should not detect overlap for adjacent ranges")
    void hasOverlap_AdjacentRanges() {
        LocalDateTime start1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 1, 15, 14, 0);

        assertFalse(dateRangeUtil.hasOverlap(start1, end1, start2, end2));
    }

    @Test
    @DisplayName("Should calculate overlap duration")
    void calculateOverlap_WithOverlap() {
        LocalDateTime start1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 1, 15, 11, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 1, 15, 13, 0);

        Duration overlap = dateRangeUtil.calculateOverlap(start1, end1, start2, end2);
        assertEquals(Duration.ofHours(1), overlap);
    }

    @Test
    @DisplayName("Should return zero duration for no overlap")
    void calculateOverlap_NoOverlap() {
        LocalDateTime start1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 1, 15, 13, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 1, 15, 15, 0);

        Duration overlap = dateRangeUtil.calculateOverlap(start1, end1, start2, end2);
        assertEquals(Duration.ZERO, overlap);
    }

    @Test
    @DisplayName("Should get intersection of overlapping ranges")
    void getIntersection_WithOverlap() {
        LocalDateTime start1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 1, 15, 11, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 1, 15, 13, 0);

        DateRangeUtil.DateRange intersection = dateRangeUtil.getIntersection(start1, end1, start2, end2);
        
        assertNotNull(intersection);
        assertEquals(LocalDateTime.of(2024, 1, 15, 11, 0), intersection.getStart());
        assertEquals(LocalDateTime.of(2024, 1, 15, 12, 0), intersection.getEnd());
    }

    @Test
    @DisplayName("Should return null for non-overlapping ranges")
    void getIntersection_NoOverlap() {
        LocalDateTime start1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 1, 15, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 1, 15, 13, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 1, 15, 15, 0);

        DateRangeUtil.DateRange intersection = dateRangeUtil.getIntersection(start1, end1, start2, end2);
        assertNull(intersection);
    }

    @Test
    @DisplayName("Should calculate business days excluding weekends")
    void calculateBusinessDays_ExcludingWeekends() {
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate end = LocalDate.of(2024, 1, 19);   // Friday

        long businessDays = dateRangeUtil.calculateBusinessDays(start, end);
        assertEquals(5, businessDays);
    }

    @Test
    @DisplayName("Should calculate business days including weekend")
    void calculateBusinessDays_IncludingWeekend() {
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate end = LocalDate.of(2024, 1, 21);   // Sunday

        long businessDays = dateRangeUtil.calculateBusinessDays(start, end);
        assertEquals(5, businessDays); // Monday to Friday only
    }

    @Test
    @DisplayName("Should return zero for invalid date range")
    void calculateBusinessDays_InvalidRange() {
        LocalDate start = LocalDate.of(2024, 1, 19); // Friday
        LocalDate end = LocalDate.of(2024, 1, 15);   // Monday

        long businessDays = dateRangeUtil.calculateBusinessDays(start, end);
        assertEquals(0, businessDays);
    }

    @Test
    @DisplayName("Should calculate business days with custom weekend days")
    void calculateBusinessDays_CustomWeekendDays() {
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate end = LocalDate.of(2024, 1, 21);   // Sunday
        Set<DayOfWeek> weekendDays = new HashSet<>(Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY));

        long businessDays = dateRangeUtil.calculateBusinessDays(start, end, weekendDays);
        assertEquals(4, businessDays); // Tuesday to Friday only
    }

    @Test
    @DisplayName("Should calculate business hours")
    void calculateBusinessHours() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 9, 0);  // Monday 9 AM
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 17, 0);   // Monday 5 PM

        Duration businessHours = dateRangeUtil.calculateBusinessHours(start, end);
        assertEquals(Duration.ofHours(8), businessHours);
    }

    @Test
    @DisplayName("Should calculate business hours across multiple days")
    void calculateBusinessHours_MultipleDays() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 9, 0);  // Monday 9 AM
        LocalDateTime end = LocalDateTime.of(2024, 1, 16, 17, 0);   // Tuesday 5 PM

        Duration businessHours = dateRangeUtil.calculateBusinessHours(start, end);
        assertEquals(Duration.ofHours(16), businessHours); // 8 hours each day
    }

    @Test
    @DisplayName("Should calculate business hours with custom business hours")
    void calculateBusinessHours_CustomHours() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 8, 0);  // Monday 8 AM
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 18, 0);   // Monday 6 PM
        LocalTime businessStart = LocalTime.of(9, 0);
        LocalTime businessEnd = LocalTime.of(17, 0);

        Duration businessHours = dateRangeUtil.calculateBusinessHours(start, end, businessStart, businessEnd);
        assertEquals(Duration.ofHours(8), businessHours); // 9 AM to 5 PM only
    }

    @Test
    @DisplayName("Should return zero duration for invalid business hours range")
    void calculateBusinessHours_InvalidRange() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 17, 0); // Monday 5 PM
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 9, 0);    // Monday 9 AM

        Duration businessHours = dateRangeUtil.calculateBusinessHours(start, end);
        assertEquals(Duration.ZERO, businessHours);
    }

    @Test
    @DisplayName("Should get all dates in range")
    void getDatesInRange() {
        LocalDate start = LocalDate.of(2024, 1, 15);
        LocalDate end = LocalDate.of(2024, 1, 17);

        List<LocalDate> dates = dateRangeUtil.getDatesInRange(start, end);
        
        assertEquals(3, dates.size());
        assertEquals(start, dates.get(0));
        assertEquals(LocalDate.of(2024, 1, 16), dates.get(1));
        assertEquals(end, dates.get(2));
    }

    @Test
    @DisplayName("Should get business dates in range")
    void getBusinessDatesInRange() {
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate end = LocalDate.of(2024, 1, 21);   // Sunday

        List<LocalDate> businessDates = dateRangeUtil.getBusinessDatesInRange(start, end);
        
        assertEquals(5, businessDates.size());
        assertTrue(businessDates.stream().allMatch(date -> 
            date.getDayOfWeek() != DayOfWeek.SATURDAY && 
            date.getDayOfWeek() != DayOfWeek.SUNDAY));
    }

    @Test
    @DisplayName("Should get next business day")
    void getNextBusinessDay() {
        LocalDate friday = LocalDate.of(2024, 1, 19); // Friday
        LocalDate nextBusinessDay = dateRangeUtil.getNextBusinessDay(friday);
        assertEquals(LocalDate.of(2024, 1, 22), nextBusinessDay); // Monday
    }

    @Test
    @DisplayName("Should get next business day from weekend")
    void getNextBusinessDay_FromWeekend() {
        LocalDate saturday = LocalDate.of(2024, 1, 20); // Saturday
        LocalDate nextBusinessDay = dateRangeUtil.getNextBusinessDay(saturday);
        assertEquals(LocalDate.of(2024, 1, 22), nextBusinessDay); // Monday
    }

    @Test
    @DisplayName("Should get previous business day")
    void getPreviousBusinessDay() {
        LocalDate monday = LocalDate.of(2024, 1, 22); // Monday
        LocalDate previousBusinessDay = dateRangeUtil.getPreviousBusinessDay(monday);
        assertEquals(LocalDate.of(2024, 1, 19), previousBusinessDay); // Friday
    }

    @Test
    @DisplayName("Should get previous business day from weekend")
    void getPreviousBusinessDay_FromWeekend() {
        LocalDate saturday = LocalDate.of(2024, 1, 20); // Saturday
        LocalDate previousBusinessDay = dateRangeUtil.getPreviousBusinessDay(saturday);
        assertEquals(LocalDate.of(2024, 1, 19), previousBusinessDay); // Friday
    }

    @Test
    @DisplayName("Should get start of week")
    void getStartOfWeek() {
        LocalDate wednesday = LocalDate.of(2024, 1, 17); // Wednesday
        LocalDate startOfWeek = dateRangeUtil.getStartOfWeek(wednesday);
        assertEquals(LocalDate.of(2024, 1, 15), startOfWeek); // Monday
    }

    @Test
    @DisplayName("Should get end of week")
    void getEndOfWeek() {
        LocalDate wednesday = LocalDate.of(2024, 1, 17); // Wednesday
        LocalDate endOfWeek = dateRangeUtil.getEndOfWeek(wednesday);
        assertEquals(LocalDate.of(2024, 1, 21), endOfWeek); // Sunday
    }

    @Test
    @DisplayName("Should get start of month")
    void getStartOfMonth() {
        LocalDate midMonth = LocalDate.of(2024, 1, 15);
        LocalDate startOfMonth = dateRangeUtil.getStartOfMonth(midMonth);
        assertEquals(LocalDate.of(2024, 1, 1), startOfMonth);
    }

    @Test
    @DisplayName("Should get end of month")
    void getEndOfMonth() {
        LocalDate midMonth = LocalDate.of(2024, 1, 15);
        LocalDate endOfMonth = dateRangeUtil.getEndOfMonth(midMonth);
        assertEquals(LocalDate.of(2024, 1, 31), endOfMonth);
    }

    @Test
    @DisplayName("Should get start of year")
    void getStartOfYear() {
        LocalDate midYear = LocalDate.of(2024, 6, 15);
        LocalDate startOfYear = dateRangeUtil.getStartOfYear(midYear);
        assertEquals(LocalDate.of(2024, 1, 1), startOfYear);
    }

    @Test
    @DisplayName("Should get end of year")
    void getEndOfYear() {
        LocalDate midYear = LocalDate.of(2024, 6, 15);
        LocalDate endOfYear = dateRangeUtil.getEndOfYear(midYear);
        assertEquals(LocalDate.of(2024, 12, 31), endOfYear);
    }

    @Test
    @DisplayName("Should convert timezone")
    void convertTimeZone() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        ZoneId fromZone = ZoneOffset.UTC;
        ZoneId toZone = ZoneOffset.UTC;

        LocalDateTime converted = dateRangeUtil.convertTimeZone(dateTime, fromZone, toZone);
        assertNotNull(converted);
        assertEquals(dateTime, converted);
    }

    @Test
    @DisplayName("Should convert to UTC")
    void toUtc() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        ZoneId fromZone = ZoneOffset.UTC;

        LocalDateTime utc = dateRangeUtil.toUtc(dateTime, fromZone);
        assertNotNull(utc);
        assertEquals(dateTime, utc);
    }

    @Test
    @DisplayName("Should convert from UTC")
    void fromUtc() {
        LocalDateTime utcDateTime = LocalDateTime.of(2024, 1, 15, 15, 0); // UTC
        ZoneId toZone = ZoneOffset.UTC;

        LocalDateTime converted = dateRangeUtil.fromUtc(utcDateTime, toZone);
        assertNotNull(converted);
        assertEquals(utcDateTime, converted);
    }

    @Test
    @DisplayName("Should identify holiday")
    void isHoliday() {
        LocalDate christmas = LocalDate.of(2024, 12, 25);
        assertTrue(dateRangeUtil.isHoliday(christmas));
    }

    @Test
    @DisplayName("Should identify non-holiday")
    void isHoliday_NonHoliday() {
        LocalDate regularDay = LocalDate.of(2024, 1, 15);
        assertFalse(dateRangeUtil.isHoliday(regularDay));
    }

    @Test
    @DisplayName("Should calculate business days excluding holidays")
    void calculateBusinessDaysExcludingHolidays() {
        LocalDate start = LocalDate.of(2024, 12, 23); // Monday before Christmas
        LocalDate end = LocalDate.of(2024, 12, 27);   // Friday after Christmas

        long businessDays = dateRangeUtil.calculateBusinessDaysExcludingHolidays(start, end);
        assertEquals(4, businessDays); // Monday, Tuesday, Thursday, Friday (Christmas is Wednesday)
    }

    @Test
    @DisplayName("Should create DateRange with correct properties")
    void dateRangeProperties() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 12, 0);
        
        DateRangeUtil.DateRange range = new DateRangeUtil.DateRange(start, end);
        
        assertEquals(start, range.getStart());
        assertEquals(end, range.getEnd());
        assertEquals(Duration.ofHours(2), range.getDuration());
    }

    @Test
    @DisplayName("Should handle edge case: same start and end time")
    void hasOverlap_SameTime() {
        LocalDateTime time = LocalDateTime.of(2024, 1, 15, 10, 0);
        
        assertFalse(dateRangeUtil.hasOverlap(time, time, time, time));
    }

    @Test
    @DisplayName("Should handle edge case: one range completely within another")
    void hasOverlap_OneWithinAnother() {
        LocalDateTime start1 = LocalDateTime.of(2024, 1, 15, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 1, 15, 13, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 1, 15, 12, 0);

        assertTrue(dateRangeUtil.hasOverlap(start1, end1, start2, end2));
    }

    @Test
    @DisplayName("Should handle leap year in business days calculation")
    void calculateBusinessDays_LeapYear() {
        LocalDate start = LocalDate.of(2024, 2, 28); // Leap year February
        LocalDate end = LocalDate.of(2024, 3, 1);

        long businessDays = dateRangeUtil.calculateBusinessDays(start, end);
        assertTrue(businessDays > 0);
    }

    @Test
    @DisplayName("Should handle timezone conversion with daylight saving time")
    void convertTimeZone_DaylightSaving() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 15, 10, 0); // Summer time
        ZoneId fromZone = ZoneId.of("America/New_York");
        ZoneId toZone = ZoneId.of("Europe/London");

        LocalDateTime converted = dateRangeUtil.convertTimeZone(dateTime, fromZone, toZone);
        assertNotNull(converted);
    }
} 