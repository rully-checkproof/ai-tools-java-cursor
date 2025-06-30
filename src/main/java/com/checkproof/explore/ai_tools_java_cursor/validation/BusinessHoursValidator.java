package com.checkproof.explore.ai_tools_java_cursor.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Validator implementation for BusinessHours annotation
 */
public class BusinessHoursValidator implements ConstraintValidator<BusinessHours, LocalDateTime> {

    private LocalTime startTime;
    private LocalTime endTime;
    private boolean allowStartTime;
    private boolean allowEndTime;

    @Override
    public void initialize(BusinessHours constraintAnnotation) {
        this.startTime = LocalTime.of(
            constraintAnnotation.startHour(), 
            constraintAnnotation.startMinute()
        );
        this.endTime = LocalTime.of(
            constraintAnnotation.endHour(), 
            constraintAnnotation.endMinute()
        );
        this.allowStartTime = constraintAnnotation.allowStartTime();
        this.allowEndTime = constraintAnnotation.allowEndTime();
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        LocalTime time = value.toLocalTime();

        if (allowStartTime && allowEndTime) {
            return !time.isBefore(startTime) && !time.isAfter(endTime);
        } else if (allowStartTime) {
            return !time.isBefore(startTime) && time.isBefore(endTime);
        } else if (allowEndTime) {
            return time.isAfter(startTime) && !time.isAfter(endTime);
        } else {
            return time.isAfter(startTime) && time.isBefore(endTime);
        }
    }
} 