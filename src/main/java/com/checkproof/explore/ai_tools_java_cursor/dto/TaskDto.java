package com.checkproof.explore.ai_tools_java_cursor.dto;

import com.checkproof.explore.ai_tools_java_cursor.model.Task;
import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private Long id;

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Task title cannot exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Task description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "Priority is required")
    private Task.Priority priority;

    @NotNull(message = "Status is required")
    private Task.TaskStatus status;

    private RecurrencePatternDto recurrencePattern;

    private Set<ParticipantDto> participants;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper method to check if task is recurring
    public boolean isRecurring() {
        return recurrencePattern != null;
    }

    // Helper method to get recurrence type
    public RecurrencePattern.RecurrenceType getRecurrenceType() {
        return recurrencePattern != null ? recurrencePattern.getRecurrenceType() : null;
    }

    // Static factory method to create DTO from entity
    public static TaskDto fromEntity(Task task) {
        if (task == null) {
            return null;
        }

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .priority(task.getPriority())
                .status(task.getStatus())
                .recurrencePattern(RecurrencePatternDto.fromEntity(task.getRecurrencePattern()))
                .participants(task.getParticipants() != null ? 
                    task.getParticipants().stream()
                        .map(ParticipantDto::fromEntity)
                        .collect(Collectors.toSet()) : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    // Method to convert DTO to entity
    public Task toEntity() {
        return Task.builder()
                .id(id)
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .priority(priority)
                .status(status)
                .recurrencePattern(recurrencePattern != null ? recurrencePattern.toEntity() : null)
                .participants(participants != null ? 
                    participants.stream()
                        .map(ParticipantDto::toEntity)
                        .collect(Collectors.toSet()) : null)
                .build();
    }
} 