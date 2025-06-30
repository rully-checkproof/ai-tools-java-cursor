package com.checkproof.explore.ai_tools_java_cursor.mapper;

import com.checkproof.explore.ai_tools_java_cursor.dto.EventDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.ParticipantDto;
import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Event entities and EventDto objects
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventMapper {

    private final ParticipantMapper participantMapper;

    /**
     * Convert Event entity to EventDto
     */
    public EventDto toDto(Event event) {
        if (event == null) {
            return null;
        }

        try {
            return EventDto.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .description(event.getDescription())
                    .eventDate(event.getEventDate())
                    .durationMinutes(event.getDurationMinutes())
                    .category(event.getCategory())
                    .participants(mapParticipantsToDto(event.getParticipants()))
                    .createdAt(event.getCreatedAt())
                    .updatedAt(event.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping Event to EventDto: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to map Event to EventDto", e);
        }
    }

    /**
     * Convert EventDto to Event entity
     */
    public Event toEntity(EventDto eventDto) {
        if (eventDto == null) {
            return null;
        }

        try {
            Event event = new Event();
            event.setId(eventDto.getId());
            event.setName(eventDto.getName());
            event.setDescription(eventDto.getDescription());
            event.setEventDate(eventDto.getEventDate());
            event.setDurationMinutes(eventDto.getDurationMinutes());
            event.setCategory(eventDto.getCategory());
            event.setCreatedAt(eventDto.getCreatedAt() != null ? eventDto.getCreatedAt() : LocalDateTime.now());
            event.setUpdatedAt(eventDto.getUpdatedAt() != null ? eventDto.getUpdatedAt() : LocalDateTime.now());
            
            // Note: Participants are typically managed separately to avoid circular references
            // and to allow for more complex participant management logic
            
            return event;
        } catch (Exception e) {
            log.error("Error mapping EventDto to Event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to map EventDto to Event", e);
        }
    }

    /**
     * Convert list of Event entities to list of EventDto objects
     */
    public List<EventDto> toDtoList(List<Event> events) {
        if (events == null) {
            return List.of();
        }

        return events.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of EventDto objects to list of Event entities
     */
    public List<Event> toEntityList(List<EventDto> eventDtos) {
        if (eventDtos == null) {
            return List.of();
        }

        return eventDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing Event entity with data from EventDto
     */
    public void updateEntityFromDto(Event existingEvent, EventDto eventDto) {
        if (existingEvent == null || eventDto == null) {
            return;
        }

        try {
            // Update fields that should be updated
            existingEvent.setName(eventDto.getName());
            existingEvent.setDescription(eventDto.getDescription());
            existingEvent.setEventDate(eventDto.getEventDate());
            existingEvent.setDurationMinutes(eventDto.getDurationMinutes());
            existingEvent.setCategory(eventDto.getCategory());
            existingEvent.setUpdatedAt(LocalDateTime.now());
            
            // Note: ID, createdAt, and participants are typically not updated here
            // ID should never change, createdAt should be preserved, and participants
            // are managed through separate endpoints
        } catch (Exception e) {
            log.error("Error updating Event entity from EventDto: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update Event entity from EventDto", e);
        }
    }

    /**
     * Create a new Event entity for creation (without ID and timestamps)
     */
    public Event toEntityForCreation(EventDto eventDto) {
        if (eventDto == null) {
            return null;
        }

        try {
            Event event = new Event();
            event.setName(eventDto.getName());
            event.setDescription(eventDto.getDescription());
            event.setEventDate(eventDto.getEventDate());
            event.setDurationMinutes(eventDto.getDurationMinutes());
            event.setCategory(eventDto.getCategory());
            event.setCreatedAt(LocalDateTime.now());
            event.setUpdatedAt(LocalDateTime.now());
            
            return event;
        } catch (Exception e) {
            log.error("Error creating Event entity from EventDto: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Event entity from EventDto", e);
        }
    }

    /**
     * Map participants from entity to DTO
     */
    private List<ParticipantDto> mapParticipantsToDto(Set<Participant> participants) {
        if (participants == null) {
            return List.of();
        }

        return participants.stream()
                .map(participantMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Map participants from DTO to entity
     */
    private Set<Participant> mapParticipantsToEntity(List<ParticipantDto> participantDtos) {
        if (participantDtos == null) {
            return Set.of();
        }

        return participantDtos.stream()
                .map(participantMapper::toEntity)
                .collect(Collectors.toSet());
    }

    /**
     * Validate EventDto for creation
     */
    public void validateForCreation(EventDto eventDto) {
        if (eventDto == null) {
            throw new IllegalArgumentException("EventDto cannot be null");
        }

        if (eventDto.getName() == null || eventDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Event name is required");
        }

        if (eventDto.getEventDate() == null) {
            throw new IllegalArgumentException("Event date is required");
        }

        if (eventDto.getCategory() == null) {
            throw new IllegalArgumentException("Event category is required");
        }
    }

    /**
     * Validate EventDto for update
     */
    public void validateForUpdate(EventDto eventDto) {
        validateForCreation(eventDto);

        if (eventDto.getId() == null) {
            throw new IllegalArgumentException("Event ID is required for updates");
        }
    }

    /**
     * Create a summary DTO with minimal information (for lists)
     */
    public EventDto toSummaryDto(Event event) {
        if (event == null) {
            return null;
        }

        return EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .eventDate(event.getEventDate())
                .durationMinutes(event.getDurationMinutes())
                .category(event.getCategory())
                .build();
    }

    /**
     * Create summary DTOs for a list of events
     */
    public List<EventDto> toSummaryDtoList(List<Event> events) {
        if (events == null) {
            return List.of();
        }

        return events.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }
} 