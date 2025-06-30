package com.checkproof.explore.ai_tools_java_cursor.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for calendar operations and calculations
 */
@Component
@Slf4j
public class CalendarUtil {

    /**
     * Get the first day of a month
     */
    public LocalDate getFirstDayOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1);
    }

    /**
     * Get the last day of a month
     */
    public LocalDate getLastDayOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * Get the first day of a week (Monday) for a given date
     */
    public LocalDate getFirstDayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Get the last day of a week (Sunday) for a given date
     */
    public LocalDate getLastDayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * Get the first day of a year
     */
    public LocalDate getFirstDayOfYear(int year) {
        return LocalDate.of(year, 1, 1);
    }

    /**
     * Get the last day of a year
     */
    public LocalDate getLastDayOfYear(int year) {
        return LocalDate.of(year, 12, 31);
    }

    /**
     * Get the week number of a date (ISO 8601 standard)
     */
    public int getWeekNumber(LocalDate date) {
        return date.get(WeekFields.ISO.weekOfWeekBasedYear());
    }

    /**
     * Get the week of year for a date
     */
    public int getWeekOfYear(LocalDate date) {
        return date.get(WeekFields.ISO.weekOfYear());
    }

    /**
     * Get the day of year for a date
     */
    public int getDayOfYear(LocalDate date) {
        return date.getDayOfYear();
    }

    /**
     * Check if a year is a leap year
     */
    public boolean isLeapYear(int year) {
        return Year.of(year).isLeap();
    }

    /**
     * Get the number of days in a month
     */
    public int getDaysInMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    /**
     * Get the number of weeks in a month
     */
    public int getWeeksInMonth(int year, int month) {
        LocalDate firstDay = getFirstDayOfMonth(year, month);
        LocalDate lastDay = getLastDayOfMonth(year, month);
        
        LocalDate firstWeekStart = getFirstDayOfWeek(firstDay);
        LocalDate lastWeekEnd = getLastDayOfWeek(lastDay);
        
        return (int) Math.ceil(
            (double) ChronoUnit.DAYS.between(firstWeekStart, lastWeekEnd.plusDays(1)) / 7
        );
    }

    /**
     * Generate a calendar grid for a month (6 weeks x 7 days)
     */
    public List<List<LocalDate>> generateMonthCalendar(int year, int month) {
        List<List<LocalDate>> calendar = new ArrayList<>();
        
        LocalDate firstDayOfMonth = getFirstDayOfMonth(year, month);
        LocalDate lastDayOfMonth = getLastDayOfMonth(year, month);
        
        LocalDate firstDayOfWeek = getFirstDayOfWeek(firstDayOfMonth);
        LocalDate lastDayOfWeek = getLastDayOfWeek(lastDayOfMonth);
        
        LocalDate currentDate = firstDayOfWeek;
        
        while (!currentDate.isAfter(lastDayOfWeek)) {
            List<LocalDate> week = new ArrayList<>();
            
            for (int i = 0; i < 7; i++) {
                week.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
            
            calendar.add(week);
        }
        
        return calendar;
    }

    /**
     * Generate a calendar grid for a week
     */
    public List<LocalDate> generateWeekCalendar(LocalDate date) {
        List<LocalDate> week = new ArrayList<>();
        LocalDate firstDayOfWeek = getFirstDayOfWeek(date);
        
        for (int i = 0; i < 7; i++) {
            week.add(firstDayOfWeek.plusDays(i));
        }
        
        return week;
    }

    /**
     * Get the month name for a given month number
     */
    public String getMonthName(int month) {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    /**
     * Get the month name abbreviation for a given month number
     */
    public String getMonthNameShort(int month) {
        return Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }

    /**
     * Get the day of week name
     */
    public String getDayOfWeekName(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    /**
     * Get the day of week name abbreviation
     */
    public String getDayOfWeekNameShort(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }

    /**
     * Get all day names for a week
     */
    public List<String> getWeekDayNames() {
        List<String> dayNames = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dayNames.add(getDayOfWeekName(day));
        }
        return dayNames;
    }

    /**
     * Get all day name abbreviations for a week
     */
    public List<String> getWeekDayNameShorts() {
        List<String> dayNames = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dayNames.add(getDayOfWeekNameShort(day));
        }
        return dayNames;
    }

    /**
     * Check if a date is today
     */
    public boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    /**
     * Check if a date is in the current month
     */
    public boolean isCurrentMonth(LocalDate date) {
        LocalDate now = LocalDate.now();
        return date.getYear() == now.getYear() && date.getMonth() == now.getMonth();
    }

    /**
     * Check if a date is in the current year
     */
    public boolean isCurrentYear(LocalDate date) {
        return date.getYear() == LocalDate.now().getYear();
    }

    /**
     * Get the current month number
     */
    public int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    /**
     * Get the current year
     */
    public int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    /**
     * Get the previous month
     */
    public MonthYear getPreviousMonth(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1).minusMonths(1);
        return new MonthYear(date.getYear(), date.getMonthValue());
    }

    /**
     * Get the next month
     */
    public MonthYear getNextMonth(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1).plusMonths(1);
        return new MonthYear(date.getYear(), date.getMonthValue());
    }

    /**
     * Get the previous week
     */
    public LocalDate getPreviousWeek(LocalDate date) {
        return date.minusWeeks(1);
    }

    /**
     * Get the next week
     */
    public LocalDate getNextWeek(LocalDate date) {
        return date.plusWeeks(1);
    }

    /**
     * Calculate the age in years
     */
    public int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Calculate the age in years, months, and days
     */
    public Period calculateAgePeriod(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now());
    }

    /**
     * Get the quarter of the year for a date
     */
    public int getQuarter(LocalDate date) {
        int month = date.getMonthValue();
        return (month - 1) / 3 + 1;
    }

    /**
     * Get the start date of a quarter
     */
    public LocalDate getQuarterStart(int year, int quarter) {
        int month = (quarter - 1) * 3 + 1;
        return LocalDate.of(year, month, 1);
    }

    /**
     * Get the end date of a quarter
     */
    public LocalDate getQuarterEnd(int year, int quarter) {
        int month = quarter * 3;
        return LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * Get the fiscal year start (assuming fiscal year starts in July)
     */
    public LocalDate getFiscalYearStart(int year) {
        return LocalDate.of(year, 7, 1);
    }

    /**
     * Get the fiscal year end (assuming fiscal year ends in June)
     */
    public LocalDate getFiscalYearEnd(int year) {
        return LocalDate.of(year + 1, 6, 30);
    }

    /**
     * Check if a date is a weekend
     */
    public boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Check if a date is a weekday
     */
    public boolean isWeekday(LocalDate date) {
        return !isWeekend(date);
    }

    /**
     * Get the number of weekdays in a date range
     */
    public long getWeekdayCount(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .filter(this::isWeekday)
                .count();
    }

    /**
     * Get the number of weekend days in a date range
     */
    public long getWeekendCount(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .filter(this::isWeekend)
                .count();
    }

    /**
     * Inner class to represent a month and year
     */
    public static class MonthYear {
        private final int year;
        private final int month;

        public MonthYear(int year, int month) {
            this.year = year;
            this.month = month;
        }

        public int getYear() { return year; }
        public int getMonth() { return month; }
        
        @Override
        public String toString() {
            return String.format("%d-%02d", year, month);
        }
    }
} 