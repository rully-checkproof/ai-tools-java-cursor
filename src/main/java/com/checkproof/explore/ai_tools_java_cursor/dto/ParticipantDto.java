package com.checkproof.explore.ai_tools_java_cursor.dto;

import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    private Participant.ParticipantType type;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Static factory method to create DTO from entity
    public static ParticipantDto fromEntity(Participant participant) {
        if (participant == null) {
            return null;
        }

        return ParticipantDto.builder()
                .id(participant.getId())
                .name(participant.getName())
                .email(participant.getEmail())
                .phoneNumber(participant.getPhoneNumber())
                .bio(participant.getBio())
                .type(participant.getType())
                .isActive(participant.getIsActive())
                .createdAt(participant.getCreatedAt())
                .updatedAt(participant.getUpdatedAt())
                .build();
    }

    // Method to convert DTO to entity
    public Participant toEntity() {
        return Participant.builder()
                .id(id)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .bio(bio)
                .type(type)
                .isActive(isActive)
                .build();
    }
} 