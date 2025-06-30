package com.checkproof.explore.ai_tools_java_cursor.repository;

import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurrencePatternRepository extends JpaRepository<RecurrencePattern, Long> {

    // Find recurrence patterns by type
    List<RecurrencePattern> findByRecurrenceTypeOrderByStartDateAsc(RecurrencePattern.RecurrenceType recurrenceType);

    // Find recurrence patterns by type with pagination
    Page<RecurrencePattern> findByRecurrenceTypeOrderByStartDateAsc(RecurrencePattern.RecurrenceType recurrenceType, Pageable pageable);

    // Find recurrence patterns by interval
    List<RecurrencePattern> findByIntervalOrderByStartDateAsc(Integer interval);

    // Find recurrence patterns by interval with pagination
    Page<RecurrencePattern> findByIntervalOrderByStartDateAsc(Integer interval, Pageable pageable);

    // Find recurrence patterns by type and interval
    List<RecurrencePattern> findByRecurrenceTypeAndIntervalOrderByStartDateAsc(RecurrencePattern.RecurrenceType recurrenceType, Integer interval);

    // Find recurrence patterns by type and interval with pagination
    Page<RecurrencePattern> findByRecurrenceTypeAndIntervalOrderByStartDateAsc(RecurrencePattern.RecurrenceType recurrenceType, Integer interval, Pageable pageable);

    // Find active recurrence patterns (not ended)
    @Query("SELECT rp FROM RecurrencePattern rp WHERE rp.endDate IS NULL OR rp.endDate >= :today ORDER BY rp.startDate ASC")
    List<RecurrencePattern> findActiveRecurrencePatterns(@Param("today") LocalDate today);

    // Find active recurrence patterns with pagination
    @Query("SELECT rp FROM RecurrencePattern rp WHERE rp.endDate IS NULL OR rp.endDate >= :today ORDER BY rp.startDate ASC")
    Page<RecurrencePattern> findActiveRecurrencePatterns(@Param("today") LocalDate today, Pageable pageable);

    // Find recurrence patterns by start date range
    @Query("SELECT rp FROM RecurrencePattern rp WHERE rp.startDate BETWEEN :startDate AND :endDate ORDER BY rp.startDate ASC")
    List<RecurrencePattern> findByStartDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find recurrence patterns by start date range with pagination
    @Query("SELECT rp FROM RecurrencePattern rp WHERE rp.startDate BETWEEN :startDate AND :endDate ORDER BY rp.startDate ASC")
    Page<RecurrencePattern> findByStartDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    // Find recurrence patterns by end date range
    @Query("SELECT rp FROM RecurrencePattern rp WHERE rp.endDate BETWEEN :startDate AND :endDate ORDER BY rp.endDate ASC")
    List<RecurrencePattern> findByEndDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find recurrence patterns by end date range with pagination
    @Query("SELECT rp FROM RecurrencePattern rp WHERE rp.endDate BETWEEN :startDate AND :endDate ORDER BY rp.endDate ASC")
    Page<RecurrencePattern> findByEndDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    // Find recurrence patterns with max occurrences
    @Query("SELECT rp FROM RecurrencePattern rp WHERE rp.maxOccurrences IS NOT NULL ORDER BY rp.startDate ASC")
    List<RecurrencePattern> findRecurrencePatternsWithMaxOccurrences();

    // Find recurrence patterns with max occurrences with pagination
    @Query("SELECT rp FROM RecurrencePattern rp WHERE rp.maxOccurrences IS NOT NULL ORDER BY rp.startDate ASC")
    Page<RecurrencePattern> findRecurrencePatternsWithMaxOccurrences(Pageable pageable);

    // Find recurrence patterns by max occurrences
    List<RecurrencePattern> findByMaxOccurrencesOrderByStartDateAsc(Integer maxOccurrences);

    // Find recurrence patterns by max occurrences with pagination
    Page<RecurrencePattern> findByMaxOccurrencesOrderByStartDateAsc(Integer maxOccurrences, Pageable pageable);

    // Find recurrence patterns by day of week
    @Query("SELECT rp FROM RecurrencePattern rp JOIN rp.daysOfWeek dow WHERE dow = :dayOfWeek ORDER BY rp.startDate ASC")
    List<RecurrencePattern> findByDaysOfWeekContaining(@Param("dayOfWeek") java.time.DayOfWeek dayOfWeek);

    // Find recurrence patterns by day of week with pagination
    @Query("SELECT rp FROM RecurrencePattern rp JOIN rp.daysOfWeek dow WHERE dow = :dayOfWeek ORDER BY rp.startDate ASC")
    Page<RecurrencePattern> findByDaysOfWeekContaining(@Param("dayOfWeek") java.time.DayOfWeek dayOfWeek, Pageable pageable);

    // Find recurrence patterns by day of month
    List<RecurrencePattern> findByDayOfMonthOrderByStartDateAsc(Integer dayOfMonth);

    // Find recurrence patterns by day of month with pagination
    Page<RecurrencePattern> findByDayOfMonthOrderByStartDateAsc(Integer dayOfMonth, Pageable pageable);

    // Find recurrence patterns by week of month
    List<RecurrencePattern> findByWeekOfMonthOrderByStartDateAsc(Integer weekOfMonth);

    // Find recurrence patterns by week of month with pagination
    Page<RecurrencePattern> findByWeekOfMonthOrderByStartDateAsc(Integer weekOfMonth, Pageable pageable);

    // Find recurrence patterns by month of year
    List<RecurrencePattern> findByMonthOfYearOrderByStartDateAsc(Integer monthOfYear);

    // Find recurrence patterns by month of year with pagination
    Page<RecurrencePattern> findByMonthOfYearOrderByStartDateAsc(Integer monthOfYear, Pageable pageable);

    // Count recurrence patterns by type
    long countByRecurrenceType(RecurrencePattern.RecurrenceType recurrenceType);

    // Count active recurrence patterns
    @Query("SELECT COUNT(rp) FROM RecurrencePattern rp WHERE rp.endDate IS NULL OR rp.endDate >= :today")
    long countActiveRecurrencePatterns(@Param("today") LocalDate today);

    // Count recurrence patterns with max occurrences
    @Query("SELECT COUNT(rp) FROM RecurrencePattern rp WHERE rp.maxOccurrences IS NOT NULL")
    long countRecurrencePatternsWithMaxOccurrences();

    // Count recurrence patterns by interval
    long countByInterval(Integer interval);
} 