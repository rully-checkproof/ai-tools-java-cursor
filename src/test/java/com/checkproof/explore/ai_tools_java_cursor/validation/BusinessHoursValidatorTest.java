package com.checkproof.explore.ai_tools_java_cursor.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BusinessHoursValidator Tests")
class BusinessHoursValidatorTest {

    @Mock
    private BusinessHours businessHoursAnnotation;

    @Mock
    private ConstraintValidatorContext context;

    private BusinessHoursValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BusinessHoursValidator();
    }

    @Test
    @DisplayName("Should initialize with default values")
    void shouldInitializeWithDefaultValues() {
        // Given
        when(businessHoursAnnotation.startHour()).thenReturn(9);
        when(businessHoursAnnotation.startMinute()).thenReturn(0);
        when(businessHoursAnnotation.endHour()).thenReturn(17);
        when(businessHoursAnnotation.endMinute()).thenReturn(0);
        when(businessHoursAnnotation.allowStartTime()).thenReturn(true);
        when(businessHoursAnnotation.allowEndTime()).thenReturn(true);

        // When
        validator.initialize(businessHoursAnnotation);

        // Then
        assertNotNull(validator);
    }

    @Test
    @DisplayName("Should initialize with custom values")
    void shouldInitializeWithCustomValues() {
        // Given
        when(businessHoursAnnotation.startHour()).thenReturn(8);
        when(businessHoursAnnotation.startMinute()).thenReturn(30);
        when(businessHoursAnnotation.endHour()).thenReturn(18);
        when(businessHoursAnnotation.endMinute()).thenReturn(30);
        when(businessHoursAnnotation.allowStartTime()).thenReturn(false);
        when(businessHoursAnnotation.allowEndTime()).thenReturn(false);

        // When
        validator.initialize(businessHoursAnnotation);

        // Then
        assertNotNull(validator);
    }

    @Test
    @DisplayName("Should return true for null value")
    void shouldReturnTrueForNullValue() {
        // Given
        setupDefaultBusinessHours();

        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate time within business hours with default settings")
    void shouldValidateTimeWithinBusinessHoursWithDefaultSettings() {
        // Given
        setupDefaultBusinessHours();
        LocalDateTime validTime = LocalDateTime.of(2024, 1, 15, 14, 30); // 2:30 PM

        // When
        boolean result = validator.isValid(validTime, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate time at start of business hours when allowed")
    void shouldValidateTimeAtStartOfBusinessHoursWhenAllowed() {
        // Given
        setupDefaultBusinessHours();
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 9, 0); // 9:00 AM

        // When
        boolean result = validator.isValid(startTime, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate time at end of business hours when allowed")
    void shouldValidateTimeAtEndOfBusinessHoursWhenAllowed() {
        // Given
        setupDefaultBusinessHours();
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 15, 17, 0); // 5:00 PM

        // When
        boolean result = validator.isValid(endTime, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject time before business hours")
    void shouldRejectTimeBeforeBusinessHours() {
        // Given
        setupDefaultBusinessHours();
        LocalDateTime beforeBusinessHours = LocalDateTime.of(2024, 1, 15, 8, 30); // 8:30 AM

        // When
        boolean result = validator.isValid(beforeBusinessHours, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject time after business hours")
    void shouldRejectTimeAfterBusinessHours() {
        // Given
        setupDefaultBusinessHours();
        LocalDateTime afterBusinessHours = LocalDateTime.of(2024, 1, 15, 17, 30); // 5:30 PM

        // When
        boolean result = validator.isValid(afterBusinessHours, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject time at start when not allowed")
    void shouldRejectTimeAtStartWhenNotAllowed() {
        // Given
        setupBusinessHoursWithStartTimeNotAllowed();
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 9, 0); // 9:00 AM

        // When
        boolean result = validator.isValid(startTime, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject time at end when not allowed")
    void shouldRejectTimeAtEndWhenNotAllowed() {
        // Given
        setupBusinessHoursWithEndTimeNotAllowed();
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 15, 17, 0); // 5:00 PM

        // When
        boolean result = validator.isValid(endTime, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate time when both start and end times are not allowed")
    void shouldValidateTimeWhenBothStartAndEndTimesAreNotAllowed() {
        // Given
        setupBusinessHoursWithBothTimesNotAllowed();
        LocalDateTime validTime = LocalDateTime.of(2024, 1, 15, 14, 30); // 2:30 PM

        // When
        boolean result = validator.isValid(validTime, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject time at start when both start and end times are not allowed")
    void shouldRejectTimeAtStartWhenBothStartAndEndTimesAreNotAllowed() {
        // Given
        setupBusinessHoursWithBothTimesNotAllowed();
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 9, 0); // 9:00 AM

        // When
        boolean result = validator.isValid(startTime, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject time at end when both start and end times are not allowed")
    void shouldRejectTimeAtEndWhenBothStartAndEndTimesAreNotAllowed() {
        // Given
        setupBusinessHoursWithBothTimesNotAllowed();
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 15, 17, 0); // 5:00 PM

        // When
        boolean result = validator.isValid(endTime, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate time with custom business hours")
    void shouldValidateTimeWithCustomBusinessHours() {
        // Given
        setupCustomBusinessHours();
        LocalDateTime validTime = LocalDateTime.of(2024, 1, 15, 10, 30); // 10:30 AM

        // When
        boolean result = validator.isValid(validTime, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject time outside custom business hours")
    void shouldRejectTimeOutsideCustomBusinessHours() {
        // Given
        setupCustomBusinessHours();
        LocalDateTime invalidTime = LocalDateTime.of(2024, 1, 15, 19, 0); // 7:00 PM

        // When
        boolean result = validator.isValid(invalidTime, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle edge case with minutes")
    void shouldHandleEdgeCaseWithMinutes() {
        // Given
        setupDefaultBusinessHours();
        LocalDateTime edgeTime = LocalDateTime.of(2024, 1, 15, 9, 1); // 9:01 AM

        // When
        boolean result = validator.isValid(edgeTime, context);

        // Then
        assertTrue(result);
    }

    private void setupDefaultBusinessHours() {
        when(businessHoursAnnotation.startHour()).thenReturn(9);
        when(businessHoursAnnotation.startMinute()).thenReturn(0);
        when(businessHoursAnnotation.endHour()).thenReturn(17);
        when(businessHoursAnnotation.endMinute()).thenReturn(0);
        when(businessHoursAnnotation.allowStartTime()).thenReturn(true);
        when(businessHoursAnnotation.allowEndTime()).thenReturn(true);
        validator.initialize(businessHoursAnnotation);
    }

    private void setupBusinessHoursWithStartTimeNotAllowed() {
        when(businessHoursAnnotation.startHour()).thenReturn(9);
        when(businessHoursAnnotation.startMinute()).thenReturn(0);
        when(businessHoursAnnotation.endHour()).thenReturn(17);
        when(businessHoursAnnotation.endMinute()).thenReturn(0);
        when(businessHoursAnnotation.allowStartTime()).thenReturn(false);
        when(businessHoursAnnotation.allowEndTime()).thenReturn(true);
        validator.initialize(businessHoursAnnotation);
    }

    private void setupBusinessHoursWithEndTimeNotAllowed() {
        when(businessHoursAnnotation.startHour()).thenReturn(9);
        when(businessHoursAnnotation.startMinute()).thenReturn(0);
        when(businessHoursAnnotation.endHour()).thenReturn(17);
        when(businessHoursAnnotation.endMinute()).thenReturn(0);
        when(businessHoursAnnotation.allowStartTime()).thenReturn(true);
        when(businessHoursAnnotation.allowEndTime()).thenReturn(false);
        validator.initialize(businessHoursAnnotation);
    }

    private void setupBusinessHoursWithBothTimesNotAllowed() {
        when(businessHoursAnnotation.startHour()).thenReturn(9);
        when(businessHoursAnnotation.startMinute()).thenReturn(0);
        when(businessHoursAnnotation.endHour()).thenReturn(17);
        when(businessHoursAnnotation.endMinute()).thenReturn(0);
        when(businessHoursAnnotation.allowStartTime()).thenReturn(false);
        when(businessHoursAnnotation.allowEndTime()).thenReturn(false);
        validator.initialize(businessHoursAnnotation);
    }

    private void setupCustomBusinessHours() {
        when(businessHoursAnnotation.startHour()).thenReturn(8);
        when(businessHoursAnnotation.startMinute()).thenReturn(30);
        when(businessHoursAnnotation.endHour()).thenReturn(16);
        when(businessHoursAnnotation.endMinute()).thenReturn(30);
        when(businessHoursAnnotation.allowStartTime()).thenReturn(true);
        when(businessHoursAnnotation.allowEndTime()).thenReturn(true);
        validator.initialize(businessHoursAnnotation);
    }
} 