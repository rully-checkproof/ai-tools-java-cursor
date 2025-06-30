package com.checkproof.explore.ai_tools_java_cursor.dto;

import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Event entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private Integer durationMinutes;
    private Event.EventCategory category;
    private List<ParticipantDto> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert Event entity to EventDto
     */
    public static EventDto fromEntity(Event event) {
        if (event == null) {
            return null;
        }

        return EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .durationMinutes(event.getDurationMinutes())
                .category(event.getCategory())
                .participants(event.getParticipants() != null ? 
                    event.getParticipants().stream()
                        .map(ParticipantDto::fromEntity)
                        .collect(Collectors.toList()) : null)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    /**
     * Convert EventDto to Event entity
     */
    public Event toEntity() {
        Event event = new Event();
        event.setId(this.id);
        event.setName(this.name);
        event.setDescription(this.description);
        event.setEventDate(this.eventDate);
        event.setDurationMinutes(this.durationMinutes);
        event.setCategory(this.category);
        event.setCreatedAt(this.createdAt);
        event.setUpdatedAt(this.updatedAt);
        return event;
    }

    /**
     * Convert list of Event entities to list of EventDto
     */
    public static List<EventDto> fromEntityList(List<Event> events) {
        if (events == null) {
            return List.of();
        }
        return events.stream()
                .map(EventDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get event duration
     */
    public Duration getDuration() {
        return durationMinutes != null ? Duration.ofMinutes(durationMinutes) : null;
    }

    /**
     * Set event duration
     */
    public void setDuration(Duration duration) {
        this.durationMinutes = duration != null ? (int) duration.toMinutes() : null;
    }

    /**
     * Check if event is currently active
     */
    public boolean isActive() {
        if (eventDate == null || durationMinutes == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = eventDate.plusMinutes(durationMinutes);
        return !eventDate.isAfter(now) && !endTime.isBefore(now);
    }

    /**
     * Check if event is in the past
     */
    public boolean isPast() {
        if (eventDate == null || durationMinutes == null) {
            return false;
        }
        LocalDateTime endTime = eventDate.plusMinutes(durationMinutes);
        return endTime.isBefore(LocalDateTime.now());
    }

    /**
     * Check if event is in the future
     */
    public boolean isFuture() {
        return eventDate != null && eventDate.isAfter(LocalDateTime.now());
    }

    /**
     * Get participant count
     */
    public int getParticipantCount() {
        return participants != null ? participants.size() : 0;
    }

    /**
     * Get event end time
     */
    public LocalDateTime getEndTime() {
        if (eventDate == null || durationMinutes == null) {
            return eventDate;
        }
        return eventDate.plusMinutes(durationMinutes);
    }
} 