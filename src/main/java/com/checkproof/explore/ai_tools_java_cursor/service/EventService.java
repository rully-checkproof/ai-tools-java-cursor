package com.checkproof.explore.ai_tools_java_cursor.service;

import com.checkproof.explore.ai_tools_java_cursor.dto.EventDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginatedResponseDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginationRequestDto;
import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import com.checkproof.explore.ai_tools_java_cursor.model.Participant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    // CRUD Operations
    EventDto createEvent(EventDto eventDto);
    EventDto getEventById(Long id);
    EventDto updateEvent(Long id, EventDto eventDto);
    void deleteEvent(Long id);
    
    // Calendar View Functionality
    List<EventDto> getEventsForMonth(int year, int month);
    List<EventDto> getEventsForWeek(LocalDate startDate);
    List<EventDto> getEventsForDay(LocalDate date);
    
    // Paginated Operations
    PaginatedResponseDto<EventDto> getAllEvents(PaginationRequestDto paginationRequest);
    PaginatedResponseDto<EventDto> getEventsByDateRange(LocalDate startDate, LocalDate endDate, PaginationRequestDto paginationRequest);
    PaginatedResponseDto<EventDto> getUpcomingEvents(PaginationRequestDto paginationRequest);
    PaginatedResponseDto<EventDto> getEvents(PaginationRequestDto paginationRequest);
    PaginatedResponseDto<EventDto> getEventsByCategory(Event.EventCategory category, PaginationRequestDto paginationRequest);
    PaginatedResponseDto<EventDto> searchEvents(String searchTerm, PaginationRequestDto paginationRequest);
    
    // Event Conflict Detection
    boolean hasTimeConflict(EventDto eventDto);
    List<EventDto> findConflictingEvents(EventDto eventDto);
    boolean isTimeSlotAvailable(LocalDateTime startTime, LocalDateTime endTime, Long excludeEventId);
    
    // Participant Management
    EventDto addParticipantToEvent(Long eventId, Long participantId);
    EventDto removeParticipantFromEvent(Long eventId, Long participantId);
    Set<Participant> getEventParticipants(Long eventId);
    List<EventDto> getEventsByParticipant(Long participantId);
    
    // Search and Filter
    List<EventDto> searchEventsByName(String name);
    List<EventDto> searchEventsByDescription(String description);
    
    // Additional Calendar Operations
    List<EventDto> getUpcomingEvents(int limit);
    List<EventDto> getPastEvents(int limit);
    List<EventDto> getEventsByDate(LocalDate date);
    List<EventDto> getEventsByWeek(LocalDate weekStart);
    List<EventDto> getEventsByMonth(int year, int month);
    
    // Event Reminder Scheduling
    void scheduleEventReminder(Long eventId, LocalDateTime reminderTime);
    void cancelEventReminder(Long eventId);
    List<EventDto> getEventsWithUpcomingReminders(LocalDateTime from, LocalDateTime to);
    
    // Statistics and Analytics
    long getEventCountByCategory(Event.EventCategory category);
    long getUpcomingEventCount();
    long getTodayEventCount();
    long getEventCountByMonth(int year, int month);
} 