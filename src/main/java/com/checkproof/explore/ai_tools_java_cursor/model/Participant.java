package com.checkproof.explore.ai_tools_java_cursor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    @Column(nullable = false)
    private String name;

    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Column(unique = true)
    private String email;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ParticipantType type = ParticipantType.INDIVIDUAL;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Event> events = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ParticipantType {
        INDIVIDUAL, TEAM, DEPARTMENT, ORGANIZATION
    }
} 