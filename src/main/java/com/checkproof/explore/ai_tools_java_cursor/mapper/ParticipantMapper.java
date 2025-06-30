package com.checkproof.explore.ai_tools_java_cursor.mapper;

import com.checkproof.explore.ai_tools_java_cursor.dto.ParticipantDto;
import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Participant entities and ParticipantDto objects
 */
@Component
@Slf4j
public class ParticipantMapper {

    /**
     * Convert Participant entity to ParticipantDto
     */
    public ParticipantDto toDto(Participant participant) {
        if (participant == null) {
            return null;
        }

        try {
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
        } catch (Exception e) {
            log.error("Error mapping Participant to ParticipantDto: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to map Participant to ParticipantDto", e);
        }
    }

    /**
     * Convert ParticipantDto to Participant entity
     */
    public Participant toEntity(ParticipantDto participantDto) {
        if (participantDto == null) {
            return null;
        }

        try {
            Participant participant = new Participant();
            participant.setId(participantDto.getId());
            participant.setName(participantDto.getName());
            participant.setEmail(participantDto.getEmail());
            participant.setPhoneNumber(participantDto.getPhoneNumber());
            participant.setBio(participantDto.getBio());
            participant.setType(participantDto.getType());
            participant.setIsActive(participantDto.getIsActive());
            participant.setCreatedAt(participantDto.getCreatedAt() != null ? participantDto.getCreatedAt() : LocalDateTime.now());
            participant.setUpdatedAt(participantDto.getUpdatedAt() != null ? participantDto.getUpdatedAt() : LocalDateTime.now());
            
            return participant;
        } catch (Exception e) {
            log.error("Error mapping ParticipantDto to Participant: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to map ParticipantDto to Participant", e);
        }
    }

    /**
     * Convert list of Participant entities to list of ParticipantDto objects
     */
    public List<ParticipantDto> toDtoList(List<Participant> participants) {
        if (participants == null) {
            return List.of();
        }

        return participants.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of ParticipantDto objects to list of Participant entities
     */
    public List<Participant> toEntityList(List<ParticipantDto> participantDtos) {
        if (participantDtos == null) {
            return List.of();
        }

        return participantDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing Participant entity with data from ParticipantDto
     */
    public void updateEntityFromDto(Participant existingParticipant, ParticipantDto participantDto) {
        if (existingParticipant == null || participantDto == null) {
            return;
        }

        try {
            // Update fields that should be updated
            existingParticipant.setName(participantDto.getName());
            existingParticipant.setEmail(participantDto.getEmail());
            existingParticipant.setPhoneNumber(participantDto.getPhoneNumber());
            existingParticipant.setBio(participantDto.getBio());
            existingParticipant.setType(participantDto.getType());
            existingParticipant.setIsActive(participantDto.getIsActive());
            existingParticipant.setUpdatedAt(LocalDateTime.now());
            
            // Note: ID and createdAt are typically not updated here
        } catch (Exception e) {
            log.error("Error updating Participant entity from ParticipantDto: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update Participant entity from ParticipantDto", e);
        }
    }

    /**
     * Create a new Participant entity for creation (without ID and timestamps)
     */
    public Participant toEntityForCreation(ParticipantDto participantDto) {
        if (participantDto == null) {
            return null;
        }

        try {
            Participant participant = new Participant();
            participant.setName(participantDto.getName());
            participant.setEmail(participantDto.getEmail());
            participant.setPhoneNumber(participantDto.getPhoneNumber());
            participant.setBio(participantDto.getBio());
            participant.setType(participantDto.getType());
            participant.setIsActive(participantDto.getIsActive());
            participant.setCreatedAt(LocalDateTime.now());
            participant.setUpdatedAt(LocalDateTime.now());
            
            return participant;
        } catch (Exception e) {
            log.error("Error creating Participant entity from ParticipantDto: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Participant entity from ParticipantDto", e);
        }
    }

    /**
     * Validate ParticipantDto for creation
     */
    public void validateForCreation(ParticipantDto participantDto) {
        if (participantDto == null) {
            throw new IllegalArgumentException("ParticipantDto cannot be null");
        }

        if (participantDto.getName() == null || participantDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name is required");
        }

        if (participantDto.getEmail() == null || participantDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Participant email is required");
        }
    }

    /**
     * Validate ParticipantDto for update
     */
    public void validateForUpdate(ParticipantDto participantDto) {
        validateForCreation(participantDto);

        if (participantDto.getId() == null) {
            throw new IllegalArgumentException("Participant ID is required for updates");
        }
    }

    /**
     * Create a summary DTO with minimal information (for lists)
     */
    public ParticipantDto toSummaryDto(Participant participant) {
        if (participant == null) {
            return null;
        }

        return ParticipantDto.builder()
                .id(participant.getId())
                .name(participant.getName())
                .email(participant.getEmail())
                .type(participant.getType())
                .isActive(participant.getIsActive())
                .build();
    }

    /**
     * Create summary DTOs for a list of participants
     */
    public List<ParticipantDto> toSummaryDtoList(List<Participant> participants) {
        if (participants == null) {
            return List.of();
        }

        return participants.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }
} 