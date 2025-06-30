package com.checkproof.explore.ai_tools_java_cursor.controller;

import com.checkproof.explore.ai_tools_java_cursor.dto.EventDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginatedResponseDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginationRequestDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.ConflictCheckResponse;
import com.checkproof.explore.ai_tools_java_cursor.dto.TimeSlotAvailabilityResponse;
import com.checkproof.explore.ai_tools_java_cursor.dto.EventStatisticsResponse;
import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import com.checkproof.explore.ai_tools_java_cursor.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Event Management", description = "APIs for managing events and calendar operations")
public class EventController {

    private final EventService eventService;

    @GetMapping("/calendar/{year}/{month}")
    @Operation(summary = "Get monthly calendar view", 
               description = "Retrieve all events for a specific month and year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Monthly events retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid year or month parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EventDto>> getMonthlyCalendar(
            @Parameter(description = "Year (e.g., 2024)", required = true)
            @PathVariable int year,
            @Parameter(description = "Month (1-12)", required = true)
            @PathVariable int month) {
        
        log.info("Fetching monthly calendar for {}/{}", month, year);
        List<EventDto> events = eventService.getEventsForMonth(year, month);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/week/{date}")
    @Operation(summary = "Get weekly calendar view", 
               description = "Retrieve all events for a week starting from the specified date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Weekly events retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EventDto>> getWeeklyCalendar(
            @Parameter(description = "Start date of the week (ISO format: YYYY-MM-DD)", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("Fetching weekly calendar starting from: {}", date);
        List<EventDto> events = eventService.getEventsForWeek(date);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/day/{date}")
    @Operation(summary = "Get daily calendar view", 
               description = "Retrieve all events for a specific day")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily events retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EventDto>> getDailyCalendar(
            @Parameter(description = "Date (ISO format: YYYY-MM-DD)", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("Fetching daily calendar for: {}", date);
        List<EventDto> events = eventService.getEventsForDay(date);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    @Operation(summary = "Create new event with conflict checking", 
               description = "Create a new event with automatic conflict detection")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Event created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid event data"),
        @ApiResponse(responseCode = "409", description = "Event conflicts with existing events"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventDto> createEvent(
            @Parameter(description = "Event details", required = true)
            @Valid @RequestBody EventDto eventDto) {
        
        log.info("Creating new event: {}", eventDto.getName());
        EventDto createdEvent = eventService.createEvent(eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing event", 
               description = "Update an event with conflict checking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid event data"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "409", description = "Event conflicts with existing events"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventDto> updateEvent(
            @Parameter(description = "Event ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated event details", required = true)
            @Valid @RequestBody EventDto eventDto) {
        
        log.info("Updating event with ID: {}", id);
        EventDto updatedEvent = eventService.updateEvent(id, eventDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel/delete event", 
               description = "Cancel or delete an event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Event cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> cancelEvent(
            @Parameter(description = "Event ID", required = true)
            @PathVariable Long id) {
        
        log.info("Cancelling event with ID: {}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search events by title or description", 
               description = "Search for events by title or description containing the specified text")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EventDto>> searchEvents(
            @Parameter(description = "Search term for event title", required = false)
            @RequestParam(required = false) String title,
            @Parameter(description = "Search term for event description", required = false)
            @RequestParam(required = false) String description) {
        
        log.info("Searching events - title: {}, description: {}", title, description);
        
        if (title != null && !title.trim().isEmpty()) {
            List<EventDto> events = eventService.searchEventsByName(title);
            return ResponseEntity.ok(events);
        } else if (description != null && !description.trim().isEmpty()) {
            List<EventDto> events = eventService.searchEventsByDescription(description);
            return ResponseEntity.ok(events);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all events with pagination", 
               description = "Retrieve a paginated list of all events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaginatedResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaginatedResponseDto<EventDto>> getAllEvents(
            @Parameter(description = "Pagination request parameters")
            @Valid PaginationRequestDto paginationRequest) {
        
        log.info("Fetching all events with pagination: {}", paginationRequest);
        PaginatedResponseDto<EventDto> response = eventService.getAllEvents(paginationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific event by ID", 
               description = "Retrieve a specific event by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event found and retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventDto> getEventById(
            @Parameter(description = "Event ID", required = true)
            @PathVariable Long id) {
        
        log.info("Fetching event with ID: {}", id);
        EventDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events", 
               description = "Retrieve upcoming events within the next 7 days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming events retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaginatedResponseDto<EventDto>> getUpcomingEvents(
            @Parameter(description = "Pagination request parameters")
            @Valid PaginationRequestDto paginationRequest) {
        
        log.info("Fetching upcoming events with pagination: {}", paginationRequest);
        PaginatedResponseDto<EventDto> response = eventService.getUpcomingEvents(paginationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get events in date range", 
               description = "Retrieve events within a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events in date range retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date parameters or pagination"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaginatedResponseDto<EventDto>> getEventsByDateRange(
            @Parameter(description = "Start date (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @Parameter(description = "End date (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @Parameter(description = "Pagination request parameters")
            @Valid PaginationRequestDto paginationRequest) {
        
        log.info("Fetching events in date range: {} to {}", start, end);
        PaginatedResponseDto<EventDto> response = eventService.getEventsByDateRange(start, end, paginationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get events by category", 
               description = "Retrieve events filtered by their category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events by category retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaginatedResponseDto<EventDto>> getEventsByCategory(
            @Parameter(description = "Event category", required = true)
            @PathVariable Event.EventCategory category,
            @Parameter(description = "Pagination request parameters")
            @Valid PaginationRequestDto paginationRequest) {
        
        log.info("Fetching events by category: {}", category);
        PaginatedResponseDto<EventDto> response = eventService.getEventsByCategory(category, paginationRequest);
        return ResponseEntity.ok(response);
    }

    // Participant Management Endpoints
    @PostMapping("/{eventId}/participants/{participantId}")
    @Operation(summary = "Add participant to event", 
               description = "Add a participant to a specific event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participant added successfully"),
        @ApiResponse(responseCode = "404", description = "Event or participant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventDto> addParticipantToEvent(
            @Parameter(description = "Event ID", required = true)
            @PathVariable Long eventId,
            @Parameter(description = "Participant ID", required = true)
            @PathVariable Long participantId) {
        
        log.info("Adding participant {} to event {}", participantId, eventId);
        EventDto updatedEvent = eventService.addParticipantToEvent(eventId, participantId);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{eventId}/participants/{participantId}")
    @Operation(summary = "Remove participant from event", 
               description = "Remove a participant from a specific event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participant removed successfully"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventDto> removeParticipantFromEvent(
            @Parameter(description = "Event ID", required = true)
            @PathVariable Long eventId,
            @Parameter(description = "Participant ID", required = true)
            @PathVariable Long participantId) {
        
        log.info("Removing participant {} from event {}", participantId, eventId);
        EventDto updatedEvent = eventService.removeParticipantFromEvent(eventId, participantId);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/{eventId}/participants")
    @Operation(summary = "Get event participants", 
               description = "Retrieve all participants for a specific event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event participants retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Set<Participant>> getEventParticipants(
            @Parameter(description = "Event ID", required = true)
            @PathVariable Long eventId) {
        
        log.info("Fetching participants for event: {}", eventId);
        Set<Participant> participants = eventService.getEventParticipants(eventId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/participants/{participantId}")
    @Operation(summary = "Get events by participant", 
               description = "Retrieve all events for a specific participant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participant events retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Participant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EventDto>> getEventsByParticipant(
            @Parameter(description = "Participant ID", required = true)
            @PathVariable Long participantId) {
        
        log.info("Fetching events for participant: {}", participantId);
        List<EventDto> events = eventService.getEventsByParticipant(participantId);
        return ResponseEntity.ok(events);
    }

    // Conflict Detection Endpoints
    @PostMapping("/check-conflicts")
    @Operation(summary = "Check for event conflicts", 
               description = "Check if an event conflicts with existing events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conflict check completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid event data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ConflictCheckResponse> checkConflicts(
            @Parameter(description = "Event to check for conflicts", required = true)
            @Valid @RequestBody EventDto eventDto) {
        
        log.info("Checking conflicts for event: {}", eventDto.getName());
        boolean hasConflict = eventService.hasTimeConflict(eventDto);
        List<EventDto> conflictingEvents = hasConflict ? eventService.findConflictingEvents(eventDto) : List.of();
        
        ConflictCheckResponse response = new ConflictCheckResponse(hasConflict, conflictingEvents);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/time-slot-available")
    @Operation(summary = "Check time slot availability", 
               description = "Check if a specific time slot is available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Time slot availability checked successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid time parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TimeSlotAvailabilityResponse> checkTimeSlotAvailability(
            @Parameter(description = "Start time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "Event ID to exclude from check", required = false)
            @RequestParam(required = false) Long excludeEventId) {
        
        log.info("Checking time slot availability: {} to {}", startTime, endTime);
        boolean isAvailable = eventService.isTimeSlotAvailable(startTime, endTime, excludeEventId);
        
        TimeSlotAvailabilityResponse response = new TimeSlotAvailabilityResponse(isAvailable, startTime, endTime);
        return ResponseEntity.ok(response);
    }

    // Statistics Endpoints
    @GetMapping("/statistics")
    @Operation(summary = "Get event statistics", 
               description = "Retrieve various statistics about events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EventStatisticsResponse> getEventStatistics() {
        log.info("Fetching event statistics");
        
        EventStatisticsResponse statistics = new EventStatisticsResponse(
            eventService.getUpcomingEventCount(),
            eventService.getTodayEventCount(),
            eventService.getEventCountByCategory(Event.EventCategory.MEETING),
            eventService.getEventCountByCategory(Event.EventCategory.CONFERENCE),
            eventService.getEventCountByCategory(Event.EventCategory.WORKSHOP)
        );
        
        return ResponseEntity.ok(statistics);
    }
} 