package com.checkproof.explore.ai_tools_java_cursor.service.impl;

import com.checkproof.explore.ai_tools_java_cursor.dto.EventDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginatedResponseDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginationMetadataDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginationRequestDto;
import com.checkproof.explore.ai_tools_java_cursor.exception.EventNotFoundException;
import com.checkproof.explore.ai_tools_java_cursor.exception.EventOverlapException;
import com.checkproof.explore.ai_tools_java_cursor.exception.ParticipantNotFoundException;
import com.checkproof.explore.ai_tools_java_cursor.mapper.EventMapper;
import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import com.checkproof.explore.ai_tools_java_cursor.repository.EventRepository;
import com.checkproof.explore.ai_tools_java_cursor.repository.ParticipantRepository;
import com.checkproof.explore.ai_tools_java_cursor.service.EventService;
import com.checkproof.explore.ai_tools_java_cursor.util.CalendarUtil;
import com.checkproof.explore.ai_tools_java_cursor.util.DateRangeUtil;
import com.checkproof.explore.ai_tools_java_cursor.util.RecurrenceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final EventMapper eventMapper;
    private final CalendarUtil calendarUtil;
    private final DateRangeUtil dateRangeUtil;
    private final RecurrenceUtil recurrenceUtil;

    @Override
    public List<EventDto> getEventsForMonth(int year, int month) {
        log.info("Fetching events for month: {}/{}", month, year);
        
        List<Event> events = eventRepository.findEventsByMonthAndYear(year, month);
        return eventMapper.toDtoList(events);
    }

    @Override
    public List<EventDto> getEventsForWeek(LocalDate startDate) {
        log.info("Fetching events for week starting: {}", startDate);
        
        LocalDate endDate = startDate.plusDays(6);
        List<Event> events = eventRepository.findEventsByDateRange(
            startDate.atStartOfDay(), 
            endDate.atTime(23, 59, 59)
        );
        
        return eventMapper.toDtoList(events);
    }

    @Override
    public List<EventDto> getEventsForDay(LocalDate date) {
        log.info("Fetching events for day: {}", date);
        
        List<Event> events = eventRepository.findEventsByDate(date.atStartOfDay());
        return eventMapper.toDtoList(events);
    }

    @Override
    public EventDto createEvent(EventDto eventDto) {
        log.info("Creating new event: {}", eventDto.getName());
        
        // Validate the event DTO
        eventMapper.validateForCreation(eventDto);
        
        // Check for time conflicts
        if (hasTimeConflict(eventDto)) {
            throw new EventOverlapException("Event conflicts with existing events");
        }
        
        // Convert DTO to entity for creation
        Event event = eventMapper.toEntityForCreation(eventDto);
        
        // Save the event
        Event savedEvent = eventRepository.save(event);
        
        log.info("Event created successfully with ID: {}", savedEvent.getId());
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto updateEvent(Long id, EventDto eventDto) {
        log.info("Updating event with ID: {}", id);
        
        // Validate the event DTO
        eventMapper.validateForUpdate(eventDto);
        
        // Find existing event
        Event existingEvent = eventRepository.findById(id)
            .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + id));
        
        // Check for time conflicts (excluding the current event)
        if (hasTimeConflictExcludingEvent(eventDto, id)) {
            throw new EventOverlapException("Event conflicts with existing events");
        }
        
        // Update the entity with DTO data
        eventMapper.updateEntityFromDto(existingEvent, eventDto);
        
        // Save the updated event
        Event updatedEvent = eventRepository.save(existingEvent);
        
        log.info("Event updated successfully with ID: {}", updatedEvent.getId());
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public void deleteEvent(Long id) {
        log.info("Deleting event with ID: {}", id);
        
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("Event not found with ID: " + id);
        }
        
        eventRepository.deleteById(id);
        log.info("Event deleted successfully with ID: {}", id);
    }

    @Override
    public List<EventDto> searchEventsByName(String name) {
        log.info("Searching events by name: {}", name);
        
        List<Event> events = eventRepository.findByNameContainingIgnoreCase(name);
        return eventMapper.toDtoList(events);
    }

    @Override
    public List<EventDto> searchEventsByDescription(String description) {
        log.info("Searching events by description: {}", description);
        
        List<Event> events = eventRepository.findByDescriptionContainingIgnoreCase(description);
        return eventMapper.toDtoList(events);
    }

    @Override
    public PaginatedResponseDto<EventDto> getAllEvents(PaginationRequestDto paginationRequest) {
        log.info("Fetching all events with pagination: {}", paginationRequest);
        
        Pageable pageable = PageRequest.of(
            paginationRequest.getPage(), 
            paginationRequest.getSize()
        );
        
        Page<Event> eventPage = eventRepository.findAll(pageable);
        
        List<EventDto> eventDtos = eventMapper.toDtoList(eventPage.getContent());
        
        PaginationMetadataDto metadata = PaginationMetadataDto.builder()
            .pageNumber(paginationRequest.getPage())
            .pageSize(paginationRequest.getSize())
            .totalElements(eventPage.getTotalElements())
            .totalPages(eventPage.getTotalPages())
            .hasNext(eventPage.hasNext())
            .hasPrevious(eventPage.hasPrevious())
            .build();
        
        return new PaginatedResponseDto<>(eventDtos, metadata);
    }

    @Override
    public EventDto getEventById(Long id) {
        log.info("Fetching event with ID: {}", id);
        
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + id));
        
        return eventMapper.toDto(event);
    }

    @Override
    public PaginatedResponseDto<EventDto> getUpcomingEvents(PaginationRequestDto paginationRequest) {
        log.info("Fetching upcoming events with pagination: {}", paginationRequest);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfWeek = now.plusDays(7);
        
        Pageable pageable = PageRequest.of(
            paginationRequest.getPage(), 
            paginationRequest.getSize()
        );
        
        Page<Event> eventPage = eventRepository.findUpcomingEvents(now, endOfWeek, pageable);
        
        List<EventDto> eventDtos = eventMapper.toDtoList(eventPage.getContent());
        
        PaginationMetadataDto metadata = PaginationMetadataDto.builder()
            .pageNumber(paginationRequest.getPage())
            .pageSize(paginationRequest.getSize())
            .totalElements(eventPage.getTotalElements())
            .totalPages(eventPage.getTotalPages())
            .hasNext(eventPage.hasNext())
            .hasPrevious(eventPage.hasPrevious())
            .build();
        
        return new PaginatedResponseDto<>(eventDtos, metadata);
    }

    @Override
    public PaginatedResponseDto<EventDto> getEventsByDateRange(LocalDate startDate, LocalDate endDate, PaginationRequestDto paginationRequest) {
        log.info("Fetching events in date range: {} to {}", startDate, endDate);
        
        Pageable pageable = PageRequest.of(
            paginationRequest.getPage(), 
            paginationRequest.getSize()
        );
        
        Page<Event> eventPage = eventRepository.findEventsByDateRange(
            startDate.atStartOfDay(), 
            endDate.atTime(23, 59, 59), 
            pageable
        );
        
        List<EventDto> eventDtos = eventMapper.toDtoList(eventPage.getContent());
        
        PaginationMetadataDto metadata = PaginationMetadataDto.builder()
            .pageNumber(paginationRequest.getPage())
            .pageSize(paginationRequest.getSize())
            .totalElements(eventPage.getTotalElements())
            .totalPages(eventPage.getTotalPages())
            .hasNext(eventPage.hasNext())
            .hasPrevious(eventPage.hasPrevious())
            .build();
        
        return new PaginatedResponseDto<>(eventDtos, metadata);
    }

    @Override
    public PaginatedResponseDto<EventDto> getEventsByCategory(Event.EventCategory category, PaginationRequestDto paginationRequest) {
        log.info("Fetching events by category: {}", category);
        
        Pageable pageable = PageRequest.of(
            paginationRequest.getPage(), 
            paginationRequest.getSize()
        );
        
        Page<Event> eventPage = eventRepository.findByCategoryOrderByEventDateAsc(category, pageable);
        
        List<EventDto> eventDtos = eventMapper.toDtoList(eventPage.getContent());
        
        PaginationMetadataDto metadata = PaginationMetadataDto.builder()
            .pageNumber(paginationRequest.getPage())
            .pageSize(paginationRequest.getSize())
            .totalElements(eventPage.getTotalElements())
            .totalPages(eventPage.getTotalPages())
            .hasNext(eventPage.hasNext())
            .hasPrevious(eventPage.hasPrevious())
            .build();
        
        return new PaginatedResponseDto<>(eventDtos, metadata);
    }

    @Override
    public EventDto addParticipantToEvent(Long eventId, Long participantId) {
        log.info("Adding participant {} to event {}", participantId, eventId);
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));
        
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new ParticipantNotFoundException("Participant not found with ID: " + participantId));
        
        event.getParticipants().add(participant);
        Event updatedEvent = eventRepository.save(event);
        
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public EventDto removeParticipantFromEvent(Long eventId, Long participantId) {
        log.info("Removing participant {} from event {}", participantId, eventId);
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));
        
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new ParticipantNotFoundException("Participant not found with ID: " + participantId));
        
        event.getParticipants().remove(participant);
        Event updatedEvent = eventRepository.save(event);
        
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public Set<Participant> getEventParticipants(Long eventId) {
        log.info("Fetching participants for event: {}", eventId);
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));
        
        return event.getParticipants();
    }

    @Override
    public List<EventDto> getEventsByParticipant(Long participantId) {
        log.info("Fetching events for participant: {}", participantId);
        
        if (!participantRepository.existsById(participantId)) {
            throw new ParticipantNotFoundException("Participant not found with ID: " + participantId);
        }
        
        List<Event> events = eventRepository.findEventsByParticipantId(participantId);
        return eventMapper.toDtoList(events);
    }

    @Override
    public boolean hasTimeConflict(EventDto eventDto) {
        log.info("Checking time conflict for event: {}", eventDto.getName());
        
        LocalDateTime startTime = eventDto.getEventDate();
        LocalDateTime endTime = startTime.plusMinutes(eventDto.getDurationMinutes());
        
        List<Event> conflictingEvents = eventRepository.findEventsByDateRange(startTime, endTime);
        return !conflictingEvents.isEmpty();
    }

    @Override
    public List<EventDto> findConflictingEvents(EventDto eventDto) {
        log.info("Finding conflicting events for: {}", eventDto.getName());
        
        LocalDateTime startTime = eventDto.getEventDate();
        LocalDateTime endTime = startTime.plusMinutes(eventDto.getDurationMinutes());
        
        List<Event> conflictingEvents = eventRepository.findEventsByDateRange(startTime, endTime);
        return eventMapper.toDtoList(conflictingEvents);
    }

    @Override
    public boolean isTimeSlotAvailable(LocalDateTime startTime, LocalDateTime endTime, Long excludeEventId) {
        log.info("Checking time slot availability: {} to {}", startTime, endTime);
        
        List<Event> conflictingEvents = eventRepository.findEventsByDateRange(startTime, endTime);
        
        if (excludeEventId != null) {
            conflictingEvents = conflictingEvents.stream()
                .filter(event -> !event.getId().equals(excludeEventId))
                .collect(Collectors.toList());
        }
        
        return conflictingEvents.isEmpty();
    }

    @Override
    public long getUpcomingEventCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfWeek = now.plusDays(7);
        return eventRepository.countUpcomingEvents(now, endOfWeek);
    }

    @Override
    public long getTodayEventCount() {
        return eventRepository.countTodaysEvents(LocalDateTime.now());
    }

    @Override
    public long getEventCountByCategory(Event.EventCategory category) {
        return eventRepository.countByCategory(category);
    }

    @Override
    public PaginatedResponseDto<EventDto> getEvents(PaginationRequestDto paginationRequest) {
        // This is the same as getAllEvents, just an alias
        return getAllEvents(paginationRequest);
    }

    @Override
    public PaginatedResponseDto<EventDto> searchEvents(String searchTerm, PaginationRequestDto paginationRequest) {
        log.info("Searching events with term: {}", searchTerm);
        
        Pageable pageable = PageRequest.of(
            paginationRequest.getPage(), 
            paginationRequest.getSize()
        );
        
        Page<Event> eventPage = eventRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
        
        List<EventDto> eventDtos = eventMapper.toDtoList(eventPage.getContent());
        
        PaginationMetadataDto metadata = PaginationMetadataDto.builder()
            .pageNumber(paginationRequest.getPage())
            .pageSize(paginationRequest.getSize())
            .totalElements(eventPage.getTotalElements())
            .totalPages(eventPage.getTotalPages())
            .hasNext(eventPage.hasNext())
            .hasPrevious(eventPage.hasPrevious())
            .build();
        
        return new PaginatedResponseDto<>(eventDtos, metadata);
    }

    @Override
    public List<EventDto> getUpcomingEvents(int limit) {
        log.info("Fetching upcoming events with limit: {}", limit);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfWeek = now.plusDays(7);
        
        List<Event> events = eventRepository.findUpcomingEvents(now, endOfWeek);
        
        return events.stream()
            .limit(limit)
            .map(eventMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getPastEvents(int limit) {
        log.info("Fetching past events with limit: {}", limit);
        
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventRepository.findPastEvents(now);
        
        return events.stream()
            .limit(limit)
            .map(eventMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getEventsByDate(LocalDate date) {
        log.info("Fetching events for date: {}", date);
        
        List<Event> events = eventRepository.findEventsByDate(date.atStartOfDay());
        return eventMapper.toDtoList(events);
    }

    @Override
    public List<EventDto> getEventsByWeek(LocalDate weekStart) {
        log.info("Fetching events for week starting: {}", weekStart);
        
        LocalDate weekEnd = weekStart.plusDays(6);
        List<Event> events = eventRepository.findEventsByDateRange(
            weekStart.atStartOfDay(), 
            weekEnd.atTime(23, 59, 59)
        );
        
        return eventMapper.toDtoList(events);
    }

    @Override
    public List<EventDto> getEventsByMonth(int year, int month) {
        log.info("Fetching events for month: {}/{}", month, year);
        
        List<Event> events = eventRepository.findEventsByMonthAndYear(year, month);
        return eventMapper.toDtoList(events);
    }

    @Override
    public void scheduleEventReminder(Long eventId, LocalDateTime reminderTime) {
        log.info("Scheduling reminder for event {} at {}", eventId, reminderTime);
        // TODO: Implement reminder scheduling logic
        // This would typically involve creating a reminder entity or using a scheduling service
    }

    @Override
    public void cancelEventReminder(Long eventId) {
        log.info("Cancelling reminder for event {}", eventId);
        // TODO: Implement reminder cancellation logic
    }

    @Override
    public List<EventDto> getEventsWithUpcomingReminders(LocalDateTime from, LocalDateTime to) {
        log.info("Fetching events with upcoming reminders between {} and {}", from, to);
        // TODO: Implement reminder query logic
        return List.of();
    }

    @Override
    public long getEventCountByMonth(int year, int month) {
        log.info("Counting events for month: {}/{}", month, year);
        return eventRepository.countEventsByMonthAndYear(year, month);
    }

    private boolean hasTimeConflictExcludingEvent(EventDto eventDto, Long excludeEventId) {
        LocalDateTime startTime = eventDto.getEventDate();
        LocalDateTime endTime = startTime.plusMinutes(eventDto.getDurationMinutes());
        
        List<Event> conflictingEvents = eventRepository.findEventsByDateRange(startTime, endTime);
        
        return conflictingEvents.stream()
            .anyMatch(event -> !event.getId().equals(excludeEventId));
    }
} 