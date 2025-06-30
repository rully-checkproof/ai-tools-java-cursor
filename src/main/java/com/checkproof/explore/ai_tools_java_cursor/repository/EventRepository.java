package com.checkproof.explore.ai_tools_java_cursor.repository;

import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events by date range
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :startDate AND :endDate ORDER BY e.eventDate ASC")
    List<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    // Find events by date range with pagination
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :startDate AND :endDate ORDER BY e.eventDate ASC")
    Page<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate, 
                                     Pageable pageable);

    // Find upcoming events within next 7 days
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :now AND :sevenDaysLater ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now, 
                                  @Param("sevenDaysLater") LocalDateTime sevenDaysLater);

    // Find upcoming events with pagination
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :now AND :sevenDaysLater ORDER BY e.eventDate ASC")
    Page<Event> findUpcomingEvents(@Param("now") LocalDateTime now, 
                                  @Param("sevenDaysLater") LocalDateTime sevenDaysLater, 
                                  Pageable pageable);

    // Find events by category
    List<Event> findByCategoryOrderByEventDateAsc(Event.EventCategory category);

    // Find events by category with pagination
    Page<Event> findByCategoryOrderByEventDateAsc(Event.EventCategory category, Pageable pageable);

    // Find events by duration range
    @Query("SELECT e FROM Event e WHERE e.durationMinutes BETWEEN :minDuration AND :maxDuration ORDER BY e.eventDate ASC")
    List<Event> findEventsByDurationRange(@Param("minDuration") Integer minDuration, 
                                         @Param("maxDuration") Integer maxDuration);

    // Find events by duration range with pagination
    @Query("SELECT e FROM Event e WHERE e.durationMinutes BETWEEN :minDuration AND :maxDuration ORDER BY e.eventDate ASC")
    Page<Event> findEventsByDurationRange(@Param("minDuration") Integer minDuration, 
                                         @Param("maxDuration") Integer maxDuration, 
                                         Pageable pageable);

    // Find events by participant
    @Query("SELECT e FROM Event e JOIN e.participants p WHERE p.id = :participantId ORDER BY e.eventDate ASC")
    List<Event> findEventsByParticipantId(@Param("participantId") Long participantId);

    // Find events by participant with pagination
    @Query("SELECT e FROM Event e JOIN e.participants p WHERE p.id = :participantId ORDER BY e.eventDate ASC")
    Page<Event> findEventsByParticipantId(@Param("participantId") Long participantId, Pageable pageable);

    // Find events by name containing (case-insensitive)
    @Query("SELECT e FROM Event e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY e.eventDate ASC")
    List<Event> findByNameContainingIgnoreCase(@Param("name") String name);

    // Find events by name containing with pagination
    @Query("SELECT e FROM Event e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY e.eventDate ASC")
    Page<Event> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    // Find events by description containing (case-insensitive)
    @Query("SELECT e FROM Event e WHERE LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')) ORDER BY e.eventDate ASC")
    List<Event> findByDescriptionContainingIgnoreCase(@Param("description") String description);

    // Find events by description containing with pagination
    @Query("SELECT e FROM Event e WHERE LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')) ORDER BY e.eventDate ASC")
    Page<Event> findByDescriptionContainingIgnoreCase(@Param("description") String description, Pageable pageable);

    // Find today's events
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :startOfDay AND :endOfDay ORDER BY e.eventDate ASC")
    List<Event> findTodaysEvents(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // Find today's events with pagination
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :startOfDay AND :endOfDay ORDER BY e.eventDate ASC")
    Page<Event> findTodaysEvents(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, Pageable pageable);

    // Find events for a specific day
    @Query("SELECT e FROM Event e WHERE DATE(e.eventDate) = DATE(:date) ORDER BY e.eventDate ASC")
    List<Event> findEventsByDate(@Param("date") LocalDateTime date);

    // Find events for a specific day with pagination
    @Query("SELECT e FROM Event e WHERE DATE(e.eventDate) = DATE(:date) ORDER BY e.eventDate ASC")
    Page<Event> findEventsByDate(@Param("date") LocalDateTime date, Pageable pageable);

    // Find events for a specific month and year
    @Query("SELECT e FROM Event e WHERE YEAR(e.eventDate) = :year AND MONTH(e.eventDate) = :month ORDER BY e.eventDate ASC")
    List<Event> findEventsByMonthAndYear(@Param("year") int year, @Param("month") int month);

    // Find events for a specific month and year with pagination
    @Query("SELECT e FROM Event e WHERE YEAR(e.eventDate) = :year AND MONTH(e.eventDate) = :month ORDER BY e.eventDate ASC")
    Page<Event> findEventsByMonthAndYear(@Param("year") int year, @Param("month") int month, Pageable pageable);

    // Find past events
    @Query("SELECT e FROM Event e WHERE e.eventDate < :now ORDER BY e.eventDate DESC")
    List<Event> findPastEvents(@Param("now") LocalDateTime now);

    // Find past events with pagination
    @Query("SELECT e FROM Event e WHERE e.eventDate < :now ORDER BY e.eventDate DESC")
    Page<Event> findPastEvents(@Param("now") LocalDateTime now, Pageable pageable);

    // Count events by category
    long countByCategory(Event.EventCategory category);

    // Count upcoming events
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventDate BETWEEN :now AND :sevenDaysLater")
    long countUpcomingEvents(@Param("now") LocalDateTime now, @Param("sevenDaysLater") LocalDateTime sevenDaysLater);

    // Count today's events
    @Query("SELECT COUNT(e) FROM Event e WHERE DATE(e.eventDate) = DATE(:today)")
    long countTodaysEvents(@Param("today") LocalDateTime today);

    // Count events for a specific month and year
    @Query("SELECT COUNT(e) FROM Event e WHERE YEAR(e.eventDate) = :year AND MONTH(e.eventDate) = :month")
    long countEventsByMonthAndYear(@Param("year") int year, @Param("month") int month);
} 