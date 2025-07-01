package com.checkproof.explore.ai_tools_java_cursor.controller;

import com.checkproof.explore.ai_tools_java_cursor.dto.EventDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginatedResponseDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginationRequestDto;
import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import com.checkproof.explore.ai_tools_java_cursor.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void getMonthlyCalendar_ShouldReturnEvents() throws Exception {
        // Given
        int year = 2024;
        int month = 12;
        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Meeting 1"),
            createSampleEventDto(2L, "Meeting 2")
        );

        when(eventService.getEventsForMonth(year, month)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/calendar/{year}/{month}", year, month))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Meeting 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Meeting 2"));

        verify(eventService).getEventsForMonth(year, month);
    }

    @Test
    void getWeeklyCalendar_ShouldReturnEvents() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2024, 12, 16);
        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Weekly Meeting")
        );

        when(eventService.getEventsForWeek(date)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/week/{date}", date))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Weekly Meeting"));

        verify(eventService).getEventsForWeek(date);
    }

    @Test
    void getDailyCalendar_ShouldReturnEvents() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2024, 12, 16);
        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Daily Meeting")
        );

        when(eventService.getEventsForDay(date)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/day/{date}", date))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Daily Meeting"));

        verify(eventService).getEventsForDay(date);
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() throws Exception {
        // Given
        EventDto eventDto = createSampleEventDto(null, "New Meeting");
        EventDto createdEvent = createSampleEventDto(1L, "New Meeting");

        when(eventService.createEvent(any(EventDto.class))).thenReturn(createdEvent);

        // When & Then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Meeting"));

        verify(eventService).createEvent(any(EventDto.class));
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() throws Exception {
        // Given
        Long eventId = 1L;
        EventDto eventDto = createSampleEventDto(eventId, "Updated Meeting");
        EventDto updatedEvent = createSampleEventDto(eventId, "Updated Meeting");

        when(eventService.updateEvent(eq(eventId), any(EventDto.class))).thenReturn(updatedEvent);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Updated Meeting"));

        verify(eventService).updateEvent(eq(eventId), any(EventDto.class));
    }

    @Test
    void cancelEvent_ShouldReturnNoContent() throws Exception {
        // Given
        Long eventId = 1L;
        doNothing().when(eventService).deleteEvent(eventId);

        // When & Then
        mockMvc.perform(delete("/api/events/{id}", eventId))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEvent(eventId);
    }

    @Test
    void searchEvents_WithTitle_ShouldReturnEvents() throws Exception {
        // Given
        String title = "meeting";
        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Team Meeting"),
            createSampleEventDto(2L, "Client Meeting")
        );

        when(eventService.searchEventsByName(title)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/search")
                .param("title", title))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Team Meeting"))
                .andExpect(jsonPath("$[1].name").value("Client Meeting"));

        verify(eventService).searchEventsByName(title);
    }

    @Test
    void searchEvents_WithDescription_ShouldReturnEvents() throws Exception {
        // Given
        String description = "discussion";
        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Project Discussion")
        );

        when(eventService.searchEventsByDescription(description)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/search")
                .param("description", description))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Project Discussion"));

        verify(eventService).searchEventsByDescription(description);
    }

    @Test
    void searchEvents_WithoutParameters_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/events/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllEvents_ShouldReturnPaginatedEvents() throws Exception {
        // Given
        PaginationRequestDto paginationRequest = new PaginationRequestDto();
        paginationRequest.setPage(0);
        paginationRequest.setSize(10);

        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Event 1"),
            createSampleEventDto(2L, "Event 2")
        );

        PaginatedResponseDto<EventDto> response = PaginatedResponseDto.<EventDto>builder()
                .content(events)
                .build();

        when(eventService.getAllEvents(any(PaginationRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/events")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));

        verify(eventService).getAllEvents(any(PaginationRequestDto.class));
    }

    @Test
    void getEventById_ShouldReturnEvent() throws Exception {
        // Given
        Long eventId = 1L;
        EventDto event = createSampleEventDto(eventId, "Specific Event");

        when(eventService.getEventById(eventId)).thenReturn(event);

        // When & Then
        mockMvc.perform(get("/api/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Specific Event"));

        verify(eventService).getEventById(eventId);
    }

    @Test
    void getUpcomingEvents_ShouldReturnPaginatedEvents() throws Exception {
        // Given
        PaginationRequestDto paginationRequest = new PaginationRequestDto();
        paginationRequest.setPage(0);
        paginationRequest.setSize(10);

        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Upcoming Event 1"),
            createSampleEventDto(2L, "Upcoming Event 2")
        );

        PaginatedResponseDto<EventDto> response = PaginatedResponseDto.<EventDto>builder()
                .content(events)
                .build();

        when(eventService.getUpcomingEvents(any(PaginationRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/events/upcoming")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Upcoming Event 1"))
                .andExpect(jsonPath("$.content[1].name").value("Upcoming Event 2"));

        verify(eventService).getUpcomingEvents(any(PaginationRequestDto.class));
    }

    @Test
    void getEventsByDateRange_ShouldReturnPaginatedEvents() throws Exception {
        // Given
        LocalDate start = LocalDate.of(2024, 12, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);
        PaginationRequestDto paginationRequest = new PaginationRequestDto();
        paginationRequest.setPage(0);
        paginationRequest.setSize(10);

        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Range Event 1"),
            createSampleEventDto(2L, "Range Event 2")
        );

        PaginatedResponseDto<EventDto> response = PaginatedResponseDto.<EventDto>builder()
                .content(events)
                .build();

        when(eventService.getEventsByDateRange(eq(start), eq(end), any(PaginationRequestDto.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/events/date-range")
                .param("start", start.toString())
                .param("end", end.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Range Event 1"))
                .andExpect(jsonPath("$.content[1].name").value("Range Event 2"));

        verify(eventService).getEventsByDateRange(eq(start), eq(end), any(PaginationRequestDto.class));
    }

    @Test
    void getEventsByCategory_ShouldReturnPaginatedEvents() throws Exception {
        // Given
        Event.EventCategory category = Event.EventCategory.MEETING;
        PaginationRequestDto paginationRequest = new PaginationRequestDto();
        paginationRequest.setPage(0);
        paginationRequest.setSize(10);

        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Meeting Event 1"),
            createSampleEventDto(2L, "Meeting Event 2")
        );

        PaginatedResponseDto<EventDto> response = PaginatedResponseDto.<EventDto>builder()
                .content(events)
                .build();

        when(eventService.getEventsByCategory(eq(category), any(PaginationRequestDto.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/events/category/{category}", category)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Meeting Event 1"))
                .andExpect(jsonPath("$.content[1].name").value("Meeting Event 2"));

        verify(eventService).getEventsByCategory(eq(category), any(PaginationRequestDto.class));
    }

    @Test
    void addParticipantToEvent_ShouldReturnUpdatedEvent() throws Exception {
        // Given
        Long eventId = 1L;
        Long participantId = 1L;
        EventDto updatedEvent = createSampleEventDto(eventId, "Event with Participant");

        when(eventService.addParticipantToEvent(eventId, participantId)).thenReturn(updatedEvent);

        // When & Then
        mockMvc.perform(post("/api/events/{eventId}/participants/{participantId}", eventId, participantId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Event with Participant"));

        verify(eventService).addParticipantToEvent(eventId, participantId);
    }

    @Test
    void removeParticipantFromEvent_ShouldReturnUpdatedEvent() throws Exception {
        // Given
        Long eventId = 1L;
        Long participantId = 1L;
        EventDto updatedEvent = createSampleEventDto(eventId, "Event without Participant");

        when(eventService.removeParticipantFromEvent(eventId, participantId)).thenReturn(updatedEvent);

        // When & Then
        mockMvc.perform(delete("/api/events/{eventId}/participants/{participantId}", eventId, participantId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Event without Participant"));

        verify(eventService).removeParticipantFromEvent(eventId, participantId);
    }

    @Test
    void getEventParticipants_ShouldReturnParticipants() throws Exception {
        // Given
        Long eventId = 1L;
        Set<Participant> participants = new HashSet<>();
        participants.add(createSampleParticipant(1L, "John Doe"));
        participants.add(createSampleParticipant(2L, "Jane Smith"));

        lenient().when(eventService.getEventParticipants(eventId)).thenReturn(participants);

        // When & Then
        mockMvc.perform(get("/api/events/{eventId}/participants", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItems("John Doe", "Jane Smith")))
                .andExpect(jsonPath("$[*].id", hasItems(1, 2)));

        verify(eventService).getEventParticipants(eventId);
    }

    @Test
    void getEventsByParticipant_ShouldReturnEvents() throws Exception {
        // Given
        Long participantId = 1L;
        List<EventDto> events = Arrays.asList(
            createSampleEventDto(1L, "Participant Event 1"),
            createSampleEventDto(2L, "Participant Event 2")
        );

        when(eventService.getEventsByParticipant(participantId)).thenReturn(events);

        // When & Then
        mockMvc.perform(get("/api/events/participants/{participantId}", participantId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Participant Event 1"))
                .andExpect(jsonPath("$[1].name").value("Participant Event 2"));

        verify(eventService).getEventsByParticipant(participantId);
    }

    @Test
    void checkConflicts_ShouldReturnConflictResponse() throws Exception {
        // Given
        EventDto eventDto = createSampleEventDto(null, "Conflict Check Event");
        List<EventDto> conflictingEvents = Arrays.asList(
            createSampleEventDto(1L, "Conflicting Event")
        );

        when(eventService.hasTimeConflict(any(EventDto.class))).thenReturn(true);
        when(eventService.findConflictingEvents(any(EventDto.class))).thenReturn(conflictingEvents);

        // When & Then
        mockMvc.perform(post("/api/events/check-conflicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hasConflict").value(true))
                .andExpect(jsonPath("$.conflictingEvents").isArray())
                .andExpect(jsonPath("$.conflictingEvents[0].name").value("Conflicting Event"));

        verify(eventService).hasTimeConflict(any(EventDto.class));
        verify(eventService).findConflictingEvents(any(EventDto.class));
    }

    @Test
    void checkTimeSlotAvailability_ShouldReturnAvailabilityResponse() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 16, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 16, 11, 0);
        Long excludeEventId = 1L;

        when(eventService.isTimeSlotAvailable(eq(startTime), eq(endTime), eq(excludeEventId)))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/events/time-slot-available")
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString())
                .param("excludeEventId", excludeEventId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.startTime").value(org.hamcrest.Matchers.contains(2024, 12, 16, 10, 0)))
                .andExpect(jsonPath("$.endTime").value(org.hamcrest.Matchers.contains(2024, 12, 16, 11, 0)));

        verify(eventService).isTimeSlotAvailable(eq(startTime), eq(endTime), eq(excludeEventId));
    }

    @Test
    void getEventStatistics_ShouldReturnStatistics() throws Exception {
        // Given
        when(eventService.getUpcomingEventCount()).thenReturn(5L);
        when(eventService.getTodayEventCount()).thenReturn(2L);
        when(eventService.getEventCountByCategory(Event.EventCategory.MEETING)).thenReturn(10L);
        when(eventService.getEventCountByCategory(Event.EventCategory.CONFERENCE)).thenReturn(3L);
        when(eventService.getEventCountByCategory(Event.EventCategory.WORKSHOP)).thenReturn(1L);

        // When & Then
        mockMvc.perform(get("/api/events/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.upcomingEvents").value(5))
                .andExpect(jsonPath("$.todayEvents").value(2))
                .andExpect(jsonPath("$.meetingEvents").value(10))
                .andExpect(jsonPath("$.conferenceEvents").value(3))
                .andExpect(jsonPath("$.workshopEvents").value(1));

        verify(eventService).getUpcomingEventCount();
        verify(eventService).getTodayEventCount();
        verify(eventService).getEventCountByCategory(Event.EventCategory.MEETING);
        verify(eventService).getEventCountByCategory(Event.EventCategory.CONFERENCE);
        verify(eventService).getEventCountByCategory(Event.EventCategory.WORKSHOP);
    }

    // Helper methods
    private EventDto createSampleEventDto(Long id, String name) {
        return EventDto.builder()
                .id(id)
                .name(name)
                .description("Sample event description")
                .eventDate(LocalDateTime.of(2024, 12, 16, 10, 0))
                .durationMinutes(60)
                .category(Event.EventCategory.MEETING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Participant createSampleParticipant(Long id, String name) {
        Participant participant = new Participant();
        participant.setId(id);
        participant.setName(name);
        participant.setEmail(name.toLowerCase().replace(" ", ".") + "@example.com");
        return participant;
    }
} 