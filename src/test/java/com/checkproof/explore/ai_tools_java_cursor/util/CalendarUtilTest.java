package com.checkproof.explore.ai_tools_java_cursor.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalendarUtil Tests")
class CalendarUtilTest {

    private CalendarUtil calendarUtil;

    @BeforeEach
    void setUp() {
        calendarUtil = new CalendarUtil();
    }

    @Test
    @DisplayName("Should get first day of month")
    void getFirstDayOfMonth() {
        LocalDate firstDay = calendarUtil.getFirstDayOfMonth(2024, 1);
        assertEquals(LocalDate.of(2024, 1, 1), firstDay);
    }

    @Test
    @DisplayName("Should get last day of month")
    void getLastDayOfMonth() {
        LocalDate lastDay = calendarUtil.getLastDayOfMonth(2024, 1);
        assertEquals(LocalDate.of(2024, 1, 31), lastDay);
    }

    @Test
    @DisplayName("Should get last day of February in leap year")
    void getLastDayOfMonth_LeapYearFebruary() {
        LocalDate lastDay = calendarUtil.getLastDayOfMonth(2024, 2);
        assertEquals(LocalDate.of(2024, 2, 29), lastDay);
    }

    @Test
    @DisplayName("Should get last day of February in non-leap year")
    void getLastDayOfMonth_NonLeapYearFebruary() {
        LocalDate lastDay = calendarUtil.getLastDayOfMonth(2023, 2);
        assertEquals(LocalDate.of(2023, 2, 28), lastDay);
    }

    @Test
    @DisplayName("Should get first day of week")
    void getFirstDayOfWeek() {
        LocalDate wednesday = LocalDate.of(2024, 1, 17); // Wednesday
        LocalDate firstDayOfWeek = calendarUtil.getFirstDayOfWeek(wednesday);
        assertEquals(LocalDate.of(2024, 1, 15), firstDayOfWeek); // Monday
    }

    @Test
    @DisplayName("Should get last day of week")
    void getLastDayOfWeek() {
        LocalDate wednesday = LocalDate.of(2024, 1, 17); // Wednesday
        LocalDate lastDayOfWeek = calendarUtil.getLastDayOfWeek(wednesday);
        assertEquals(LocalDate.of(2024, 1, 21), lastDayOfWeek); // Sunday
    }

    @Test
    @DisplayName("Should get first day of year")
    void getFirstDayOfYear() {
        LocalDate firstDay = calendarUtil.getFirstDayOfYear(2024);
        assertEquals(LocalDate.of(2024, 1, 1), firstDay);
    }

    @Test
    @DisplayName("Should get last day of year")
    void getLastDayOfYear() {
        LocalDate lastDay = calendarUtil.getLastDayOfYear(2024);
        assertEquals(LocalDate.of(2024, 12, 31), lastDay);
    }

    @Test
    @DisplayName("Should get week number")
    void getWeekNumber() {
        LocalDate date = LocalDate.of(2024, 1, 15); // Monday
        int weekNumber = calendarUtil.getWeekNumber(date);
        assertTrue(weekNumber > 0);
    }

    @Test
    @DisplayName("Should get week of year")
    void getWeekOfYear() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        int weekOfYear = calendarUtil.getWeekOfYear(date);
        assertTrue(weekOfYear > 0);
    }

    @Test
    @DisplayName("Should get day of year")
    void getDayOfYear() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        int dayOfYear = calendarUtil.getDayOfYear(date);
        assertEquals(15, dayOfYear);
    }

    @Test
    @DisplayName("Should identify leap year")
    void isLeapYear() {
        assertTrue(calendarUtil.isLeapYear(2024));
        assertFalse(calendarUtil.isLeapYear(2023));
        assertTrue(calendarUtil.isLeapYear(2000));
        assertFalse(calendarUtil.isLeapYear(1900));
    }

    @Test
    @DisplayName("Should get days in month")
    void getDaysInMonth() {
        assertEquals(31, calendarUtil.getDaysInMonth(2024, 1));
        assertEquals(29, calendarUtil.getDaysInMonth(2024, 2)); // Leap year
        assertEquals(28, calendarUtil.getDaysInMonth(2023, 2)); // Non-leap year
        assertEquals(30, calendarUtil.getDaysInMonth(2024, 4));
    }

    @Test
    @DisplayName("Should get weeks in month")
    void getWeeksInMonth() {
        int weeks = calendarUtil.getWeeksInMonth(2024, 1);
        assertTrue(weeks >= 4 && weeks <= 6);
    }

    @Test
    @DisplayName("Should generate month calendar")
    void generateMonthCalendar() {
        List<List<LocalDate>> calendar = calendarUtil.generateMonthCalendar(2024, 1);
        
        assertNotNull(calendar);
        assertTrue(calendar.size() >= 4 && calendar.size() <= 6); // 4-6 weeks
        
        // Check that each week has 7 days
        for (List<LocalDate> week : calendar) {
            assertEquals(7, week.size());
        }
    }

    @Test
    @DisplayName("Should generate week calendar")
    void generateWeekCalendar() {
        LocalDate date = LocalDate.of(2024, 1, 17); // Wednesday
        List<LocalDate> week = calendarUtil.generateWeekCalendar(date);
        
        assertEquals(7, week.size());
        assertEquals(DayOfWeek.MONDAY, week.get(0).getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY, week.get(6).getDayOfWeek());
    }

    @Test
    @DisplayName("Should get month name")
    void getMonthName() {
        String monthName = calendarUtil.getMonthName(1);
        assertEquals("January", monthName);
        
        String decemberName = calendarUtil.getMonthName(12);
        assertEquals("December", decemberName);
    }

    @Test
    @DisplayName("Should get month name abbreviation")
    void getMonthNameShort() {
        String monthName = calendarUtil.getMonthNameShort(1);
        assertEquals("Jan", monthName);
        
        String decemberName = calendarUtil.getMonthNameShort(12);
        assertEquals("Dec", decemberName);
    }

    @Test
    @DisplayName("Should get day of week name")
    void getDayOfWeekName() {
        String dayName = calendarUtil.getDayOfWeekName(DayOfWeek.MONDAY);
        assertEquals("Monday", dayName);
        
        String sundayName = calendarUtil.getDayOfWeekName(DayOfWeek.SUNDAY);
        assertEquals("Sunday", sundayName);
    }

    @Test
    @DisplayName("Should get day of week name abbreviation")
    void getDayOfWeekNameShort() {
        String dayName = calendarUtil.getDayOfWeekNameShort(DayOfWeek.MONDAY);
        assertEquals("Mon", dayName);
        
        String sundayName = calendarUtil.getDayOfWeekNameShort(DayOfWeek.SUNDAY);
        assertEquals("Sun", sundayName);
    }

    @Test
    @DisplayName("Should get week day names")
    void getWeekDayNames() {
        List<String> dayNames = calendarUtil.getWeekDayNames();
        
        assertEquals(7, dayNames.size());
        assertTrue(dayNames.contains("Monday"));
        assertTrue(dayNames.contains("Sunday"));
    }

    @Test
    @DisplayName("Should get week day name abbreviations")
    void getWeekDayNameShorts() {
        List<String> dayNames = calendarUtil.getWeekDayNameShorts();
        
        assertEquals(7, dayNames.size());
        assertTrue(dayNames.contains("Mon"));
        assertTrue(dayNames.contains("Sun"));
    }

    @Test
    @DisplayName("Should identify today")
    void isToday() {
        LocalDate today = LocalDate.now();
        assertTrue(calendarUtil.isToday(today));
        
        LocalDate notToday = today.plusDays(1);
        assertFalse(calendarUtil.isToday(notToday));
    }

    @Test
    @DisplayName("Should identify current month")
    void isCurrentMonth() {
        LocalDate currentMonth = LocalDate.now();
        assertTrue(calendarUtil.isCurrentMonth(currentMonth));
        
        LocalDate nextMonth = currentMonth.plusMonths(1);
        assertFalse(calendarUtil.isCurrentMonth(nextMonth));
    }

    @Test
    @DisplayName("Should identify current year")
    void isCurrentYear() {
        LocalDate currentYear = LocalDate.now();
        assertTrue(calendarUtil.isCurrentYear(currentYear));
        
        LocalDate nextYear = currentYear.plusYears(1);
        assertFalse(calendarUtil.isCurrentYear(nextYear));
    }

    @Test
    @DisplayName("Should get current month")
    void getCurrentMonth() {
        int currentMonth = calendarUtil.getCurrentMonth();
        assertEquals(LocalDate.now().getMonthValue(), currentMonth);
    }

    @Test
    @DisplayName("Should get current year")
    void getCurrentYear() {
        int currentYear = calendarUtil.getCurrentYear();
        assertEquals(LocalDate.now().getYear(), currentYear);
    }

    @Test
    @DisplayName("Should get previous month")
    void getPreviousMonth() {
        CalendarUtil.MonthYear previous = calendarUtil.getPreviousMonth(2024, 1);
        assertEquals(2023, previous.getYear());
        assertEquals(12, previous.getMonth());
    }

    @Test
    @DisplayName("Should get previous month from January")
    void getPreviousMonth_FromJanuary() {
        CalendarUtil.MonthYear previous = calendarUtil.getPreviousMonth(2024, 1);
        assertEquals(2023, previous.getYear());
        assertEquals(12, previous.getMonth());
    }

    @Test
    @DisplayName("Should get next month")
    void getNextMonth() {
        CalendarUtil.MonthYear next = calendarUtil.getNextMonth(2024, 12);
        assertEquals(2025, next.getYear());
        assertEquals(1, next.getMonth());
    }

    @Test
    @DisplayName("Should get next month from December")
    void getNextMonth_FromDecember() {
        CalendarUtil.MonthYear next = calendarUtil.getNextMonth(2024, 12);
        assertEquals(2025, next.getYear());
        assertEquals(1, next.getMonth());
    }

    @Test
    @DisplayName("Should get previous week")
    void getPreviousWeek() {
        LocalDate date = LocalDate.of(2024, 1, 15); // Monday
        LocalDate previousWeek = calendarUtil.getPreviousWeek(date);
        assertEquals(LocalDate.of(2024, 1, 8), previousWeek);
    }

    @Test
    @DisplayName("Should get next week")
    void getNextWeek() {
        LocalDate date = LocalDate.of(2024, 1, 15); // Monday
        LocalDate nextWeek = calendarUtil.getNextWeek(date);
        assertEquals(LocalDate.of(2024, 1, 22), nextWeek);
    }

    @Test
    @DisplayName("Should calculate age")
    void calculateAge() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        int age = calendarUtil.calculateAge(birthDate);
        
        LocalDate now = LocalDate.now();
        int expectedAge = Period.between(birthDate, now).getYears();
        assertEquals(expectedAge, age);
    }

    @Test
    @DisplayName("Should calculate age period")
    void calculateAgePeriod() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Period agePeriod = calendarUtil.calculateAgePeriod(birthDate);
        
        assertNotNull(agePeriod);
        assertTrue(agePeriod.getYears() > 0);
    }

    @Test
    @DisplayName("Should get quarter")
    void getQuarter() {
        assertEquals(1, calendarUtil.getQuarter(LocalDate.of(2024, 1, 15)));
        assertEquals(1, calendarUtil.getQuarter(LocalDate.of(2024, 3, 15)));
        assertEquals(2, calendarUtil.getQuarter(LocalDate.of(2024, 4, 15)));
        assertEquals(2, calendarUtil.getQuarter(LocalDate.of(2024, 6, 15)));
        assertEquals(3, calendarUtil.getQuarter(LocalDate.of(2024, 7, 15)));
        assertEquals(3, calendarUtil.getQuarter(LocalDate.of(2024, 9, 15)));
        assertEquals(4, calendarUtil.getQuarter(LocalDate.of(2024, 10, 15)));
        assertEquals(4, calendarUtil.getQuarter(LocalDate.of(2024, 12, 15)));
    }

    @Test
    @DisplayName("Should get quarter start")
    void getQuarterStart() {
        LocalDate quarterStart = calendarUtil.getQuarterStart(2024, 1);
        assertEquals(LocalDate.of(2024, 1, 1), quarterStart);
        
        LocalDate quarter2Start = calendarUtil.getQuarterStart(2024, 2);
        assertEquals(LocalDate.of(2024, 4, 1), quarter2Start);
    }

    @Test
    @DisplayName("Should get quarter end")
    void getQuarterEnd() {
        LocalDate quarterEnd = calendarUtil.getQuarterEnd(2024, 1);
        assertEquals(LocalDate.of(2024, 3, 31), quarterEnd);
        
        LocalDate quarter2End = calendarUtil.getQuarterEnd(2024, 2);
        assertEquals(LocalDate.of(2024, 6, 30), quarter2End);
    }

    @Test
    @DisplayName("Should get fiscal year start")
    void getFiscalYearStart() {
        LocalDate fiscalStart = calendarUtil.getFiscalYearStart(2024);
        assertEquals(LocalDate.of(2024, 7, 1), fiscalStart);
    }

    @Test
    @DisplayName("Should get fiscal year end")
    void getFiscalYearEnd() {
        LocalDate fiscalEnd = calendarUtil.getFiscalYearEnd(2024);
        assertEquals(LocalDate.of(2025, 6, 30), fiscalEnd);
    }

    @Test
    @DisplayName("Should identify weekend")
    void isWeekend() {
        LocalDate saturday = LocalDate.of(2024, 1, 20); // Saturday
        LocalDate sunday = LocalDate.of(2024, 1, 21);   // Sunday
        LocalDate monday = LocalDate.of(2024, 1, 22);   // Monday
        
        assertTrue(calendarUtil.isWeekend(saturday));
        assertTrue(calendarUtil.isWeekend(sunday));
        assertFalse(calendarUtil.isWeekend(monday));
    }

    @Test
    @DisplayName("Should identify weekday")
    void isWeekday() {
        LocalDate saturday = LocalDate.of(2024, 1, 20); // Saturday
        LocalDate sunday = LocalDate.of(2024, 1, 21);   // Sunday
        LocalDate monday = LocalDate.of(2024, 1, 22);   // Monday
        
        assertFalse(calendarUtil.isWeekday(saturday));
        assertFalse(calendarUtil.isWeekday(sunday));
        assertTrue(calendarUtil.isWeekday(monday));
    }

    @Test
    @DisplayName("Should count weekdays")
    void getWeekdayCount() {
        LocalDate start = LocalDate.of(2024, 1, 15); // Monday
        LocalDate end = LocalDate.of(2024, 1, 19);   // Friday
        
        long weekdayCount = calendarUtil.getWeekdayCount(start, end);
        assertEquals(5, weekdayCount);
    }

    @Test
    @DisplayName("Should count weekends")
    void getWeekendCount() {
        LocalDate start = LocalDate.of(2024, 1, 20); // Saturday
        LocalDate end = LocalDate.of(2024, 1, 21);   // Sunday
        
        long weekendCount = calendarUtil.getWeekendCount(start, end);
        assertEquals(2, weekendCount);
    }

    @Test
    @DisplayName("Should handle MonthYear toString")
    void monthYearToString() {
        CalendarUtil.MonthYear monthYear = new CalendarUtil.MonthYear(2024, 1);
        String toString = monthYear.toString();
        assertTrue(toString.contains("2024"));
        assertTrue(toString.contains("1"));
    }

    @Test
    @DisplayName("Should handle edge case: February in leap year")
    void getDaysInMonth_LeapYearFebruary() {
        assertEquals(29, calendarUtil.getDaysInMonth(2024, 2));
        assertEquals(28, calendarUtil.getDaysInMonth(2023, 2));
    }

    @Test
    @DisplayName("Should handle edge case: month calendar with different month lengths")
    void generateMonthCalendar_DifferentMonths() {
        // Test February (shorter month)
        List<List<LocalDate>> febCalendar = calendarUtil.generateMonthCalendar(2024, 2);
        assertNotNull(febCalendar);
        
        // Test December (longer month)
        List<List<LocalDate>> decCalendar = calendarUtil.generateMonthCalendar(2024, 12);
        assertNotNull(decCalendar);
    }

    @Test
    @DisplayName("Should handle edge case: week spanning year boundary")
    void getPreviousWeek_YearBoundary() {
        LocalDate firstWeekOfYear = LocalDate.of(2024, 1, 1);
        LocalDate previousWeek = calendarUtil.getPreviousWeek(firstWeekOfYear);
        assertTrue(previousWeek.getYear() == 2023);
    }

    @Test
    @DisplayName("Should handle edge case: week spanning year boundary forward")
    void getNextWeek_YearBoundary() {
        LocalDate lastWeekOfYear = LocalDate.of(2024, 12, 30);
        LocalDate nextWeek = calendarUtil.getNextWeek(lastWeekOfYear);
        assertTrue(nextWeek.getYear() == 2025);
    }

    @Test
    @DisplayName("Should handle edge case: quarter boundaries")
    void getQuarter_Boundaries() {
        assertEquals(1, calendarUtil.getQuarter(LocalDate.of(2024, 3, 31)));
        assertEquals(2, calendarUtil.getQuarter(LocalDate.of(2024, 4, 1)));
        assertEquals(2, calendarUtil.getQuarter(LocalDate.of(2024, 6, 30)));
        assertEquals(3, calendarUtil.getQuarter(LocalDate.of(2024, 7, 1)));
        assertEquals(3, calendarUtil.getQuarter(LocalDate.of(2024, 9, 30)));
        assertEquals(4, calendarUtil.getQuarter(LocalDate.of(2024, 10, 1)));
        assertEquals(4, calendarUtil.getQuarter(LocalDate.of(2024, 12, 31)));
    }

    @Test
    @DisplayName("Should handle edge case: age calculation for recent birth")
    void calculateAge_RecentBirth() {
        LocalDate recentBirth = LocalDate.now().minusDays(1);
        int age = calendarUtil.calculateAge(recentBirth);
        assertEquals(0, age);
    }

    @Test
    @DisplayName("Should handle edge case: age calculation for future birth")
    void calculateAge_FutureBirth() {
        LocalDate futureBirth = LocalDate.now().plusDays(1);
        int age = calendarUtil.calculateAge(futureBirth);
        assertEquals(0, age);
    }
} 