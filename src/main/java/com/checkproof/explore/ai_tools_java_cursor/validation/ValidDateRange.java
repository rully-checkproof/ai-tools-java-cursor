package com.checkproof.explore.ai_tools_java_cursor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for validating date ranges
 * Ensures that start date is before end date
 */
@Documented
@Constraint(validatedBy = ValidDateRangeValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    
    String message() default "Start date must be before end date";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * The field name for the start date
     */
    String startField() default "startDate";
    
    /**
     * The field name for the end date
     */
    String endField() default "endDate";
    
    /**
     * Whether to allow start and end dates to be equal
     */
    boolean allowEqual() default false;
} 