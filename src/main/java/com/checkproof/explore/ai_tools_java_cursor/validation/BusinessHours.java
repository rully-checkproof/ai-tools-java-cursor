package com.checkproof.explore.ai_tools_java_cursor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for validating business hours
 * Ensures that the time falls within business hours (9 AM - 5 PM by default)
 */
@Documented
@Constraint(validatedBy = BusinessHoursValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessHours {
    
    String message() default "Time must be within business hours (9:00 AM - 5:00 PM)";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Start hour of business hours (24-hour format)
     */
    int startHour() default 9;
    
    /**
     * Start minute of business hours
     */
    int startMinute() default 0;
    
    /**
     * End hour of business hours (24-hour format)
     */
    int endHour() default 17;
    
    /**
     * End minute of business hours
     */
    int endMinute() default 0;
    
    /**
     * Whether to allow times exactly at the start time
     */
    boolean allowStartTime() default true;
    
    /**
     * Whether to allow times exactly at the end time
     */
    boolean allowEndTime() default true;
} 