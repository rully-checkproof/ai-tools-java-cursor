package com.checkproof.explore.ai_tools_java_cursor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recurrence_patterns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurrencePattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Recurrence type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type", nullable = false)
    private RecurrenceType recurrenceType;

    @Min(value = 1, message = "Interval must be at least 1")
    @Column(nullable = false)
    @Builder.Default
    private Integer interval = 1;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_occurrences")
    @Min(value = 1, message = "Max occurrences must be at least 1")
    private Integer maxOccurrences;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "recurrence_pattern_days_of_week",
        joinColumns = @JoinColumn(name = "recurrence_pattern_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    @Builder.Default
    private Set<DayOfWeek> daysOfWeek = new HashSet<>();

    @Column(name = "day_of_month")
    @Min(value = 1, message = "Day of month must be between 1 and 31")
    private Integer dayOfMonth;

    @Column(name = "week_of_month")
    @Min(value = 1, message = "Week of month must be between 1 and 5")
    private Integer weekOfMonth;

    @Column(name = "month_of_year")
    @Min(value = 1, message = "Month of year must be between 1 and 12")
    private Integer monthOfYear;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    public enum RecurrenceType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return (startDate == null || !today.isBefore(startDate)) &&
               (endDate == null || !today.isAfter(endDate));
    }
} 