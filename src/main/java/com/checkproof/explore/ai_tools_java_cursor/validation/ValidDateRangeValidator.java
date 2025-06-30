package com.checkproof.explore.ai_tools_java_cursor.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDateTime;

/**
 * Validator implementation for ValidDateRange annotation
 */
public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startField;
    private String endField;
    private boolean allowEqual;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
        this.allowEqual = constraintAnnotation.allowEqual();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        
        Object startValue = beanWrapper.getPropertyValue(startField);
        Object endValue = beanWrapper.getPropertyValue(endField);

        // If either field is null, let other validators handle it
        if (startValue == null || endValue == null) {
            return true;
        }

        // Ensure both values are LocalDateTime
        if (!(startValue instanceof LocalDateTime) || !(endValue instanceof LocalDateTime)) {
            return false;
        }

        LocalDateTime startDate = (LocalDateTime) startValue;
        LocalDateTime endDate = (LocalDateTime) endValue;

        if (allowEqual) {
            return !startDate.isAfter(endDate);
        } else {
            return startDate.isBefore(endDate);
        }
    }
} 