package com.checkproof.explore.ai_tools_java_cursor.validation;

import com.checkproof.explore.ai_tools_java_cursor.dto.TaskDto;
import com.checkproof.explore.ai_tools_java_cursor.exception.InvalidTaskException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom validator for Task entities with complex business rules
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskValidator implements Validator {

    private final MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return TaskDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof TaskDto taskDto) {
            validateTask(taskDto, errors);
        }
    }

    /**
     * Validate a task with comprehensive business rules
     */
    public void validateTask(TaskDto taskDto, Errors errors) {
        validateBasicFields(taskDto, errors);
        validateDateRange(taskDto, errors);
        validatePriorityAndStatus(taskDto, errors);
        validateRecurrencePattern(taskDto, errors);
        validateBusinessRules(taskDto, errors);
    }

    /**
     * Validate basic required fields
     */
    private void validateBasicFields(TaskDto taskDto, Errors errors) {
        if (taskDto.getTitle() == null || taskDto.getTitle().trim().isEmpty()) {
            addError(errors, "title", "task.title.required", "Task title is required");
        } else if (taskDto.getTitle().length() > 255) {
            addError(errors, "title", "task.title.too.long", "Task title cannot exceed 255 characters");
        }

        if (taskDto.getStartDate() == null) {
            addError(errors, "startDate", "task.startDate.required", "Start date is required");
        }

        if (taskDto.getDescription() != null && taskDto.getDescription().length() > 1000) {
            addError(errors, "description", "task.description.too.long", "Task description cannot exceed 1000 characters");
        }
    }

    /**
     * Validate date range logic
     */
    private void validateDateRange(TaskDto taskDto, Errors errors) {
        LocalDateTime startDate = taskDto.getStartDate();
        LocalDateTime endDate = taskDto.getEndDate();

        if (startDate != null) {
            // Check if start date is in the past
            if (startDate.isBefore(LocalDateTime.now())) {
                addError(errors, "startDate", "task.startDate.past", "Start date cannot be in the past");
            }
        }

        if (startDate != null && endDate != null) {
            // Check if end date is before start date
            if (endDate.isBefore(startDate)) {
                addError(errors, "endDate", "task.endDate.before.start", "End date cannot be before start date");
            }

            // Check if duration is reasonable (not more than 1 year)
            if (startDate.plusYears(1).isBefore(endDate)) {
                addError(errors, "endDate", "task.duration.too.long", "Task duration cannot exceed 1 year");
            }
        }
    }

    /**
     * Validate priority and status combinations
     */
    private void validatePriorityAndStatus(TaskDto taskDto, Errors errors) {
        if (taskDto.getPriority() != null && taskDto.getStatus() != null) {
            // High priority tasks should not be cancelled
            if (taskDto.getPriority().name().equals("HIGH") && 
                taskDto.getStatus().name().equals("CANCELLED")) {
                addError(errors, "priority", "task.priority.status.conflict", 
                        "High priority tasks should not be cancelled");
            }

            // Completed tasks should not have urgent priority
            if (taskDto.getStatus().name().equals("COMPLETED") && 
                taskDto.getPriority().name().equals("URGENT")) {
                addError(errors, "status", "task.status.priority.conflict", 
                        "Completed tasks should not have urgent priority");
            }
        }
    }

    /**
     * Validate recurrence pattern
     */
    private void validateRecurrencePattern(TaskDto taskDto, Errors errors) {
        if (taskDto.getRecurrencePattern() != null) {
            var pattern = taskDto.getRecurrencePattern();
            
            if (pattern.getInterval() <= 0) {
                addError(errors, "recurrencePattern.interval", "task.recurrence.interval.invalid", 
                        "Recurrence interval must be positive");
            }

            if (pattern.getInterval() > 100) {
                addError(errors, "recurrencePattern.interval", "task.recurrence.interval.too.large", 
                        "Recurrence interval cannot exceed 100");
            }

            // Validate end date for recurring tasks
            if (taskDto.getEndDate() != null && taskDto.getStartDate() != null) {
                long daysBetween = java.time.Duration.between(taskDto.getStartDate(), taskDto.getEndDate()).toDays();
                
                if (pattern.getRecurrenceType().name().equals("DAILY") && daysBetween < pattern.getInterval()) {
                    addError(errors, "endDate", "task.recurrence.endDate.too.soon", 
                            "End date is too soon for the specified recurrence pattern");
                }
            }
        }
    }

    /**
     * Validate business-specific rules
     */
    private void validateBusinessRules(TaskDto taskDto, Errors errors) {
        // Check for weekend scheduling restrictions
        if (taskDto.getStartDate() != null) {
            var dayOfWeek = taskDto.getStartDate().getDayOfWeek();
            if (dayOfWeek.name().equals("SATURDAY") || dayOfWeek.name().equals("SUNDAY")) {
                addError(errors, "startDate", "task.startDate.weekend", 
                        "Tasks should not be scheduled on weekends");
            }
        }

        // Check for business hours restrictions
        if (taskDto.getStartDate() != null) {
            var hour = taskDto.getStartDate().getHour();
            if (hour < 8 || hour > 18) {
                addError(errors, "startDate", "task.startDate.business.hours", 
                        "Tasks should be scheduled during business hours (8 AM - 6 PM)");
            }
        }

        // Validate participant count
        if (taskDto.getParticipants() != null && taskDto.getParticipants().size() > 10) {
            addError(errors, "participants", "task.participants.too.many", 
                    "Task cannot have more than 10 participants");
        }
    }

    /**
     * Validate task for creation
     */
    public void validateForCreation(TaskDto taskDto) {
        List<String> errors = new ArrayList<>();
        
        if (taskDto.getTitle() == null || taskDto.getTitle().trim().isEmpty()) {
            errors.add(getMessage("task.title.required", "Task title is required"));
        }
        
        if (taskDto.getStartDate() == null) {
            errors.add(getMessage("task.startDate.required", "Start date is required"));
        }
        
        if (taskDto.getStartDate() != null && taskDto.getStartDate().isBefore(LocalDateTime.now())) {
            errors.add(getMessage("task.startDate.past", "Start date cannot be in the past"));
        }
        
        if (!errors.isEmpty()) {
            throw new InvalidTaskException("Task validation failed: " + String.join(", ", errors));
        }
    }

    /**
     * Validate task for update
     */
    public void validateForUpdate(TaskDto taskDto, Long taskId) {
        List<String> errors = new ArrayList<>();
        
        if (taskId == null) {
            errors.add(getMessage("task.id.required", "Task ID is required for updates"));
        }
        
        if (taskDto.getTitle() == null || taskDto.getTitle().trim().isEmpty()) {
            errors.add(getMessage("task.title.required", "Task title is required"));
        }
        
        if (taskDto.getStartDate() != null && taskDto.getStartDate().isBefore(LocalDateTime.now())) {
            errors.add(getMessage("task.startDate.past", "Start date cannot be in the past"));
        }
        
        if (!errors.isEmpty()) {
            throw new InvalidTaskException("Task update validation failed: " + String.join(", ", errors));
        }
    }

    /**
     * Add validation error with internationalized message
     */
    private void addError(Errors errors, String field, String messageCode, String defaultMessage) {
        String message = getMessage(messageCode, defaultMessage);
        errors.rejectValue(field, messageCode, message);
    }

    /**
     * Get internationalized message
     */
    private String getMessage(String messageCode, String defaultMessage) {
        try {
            return messageSource.getMessage(messageCode, null, defaultMessage, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.warn("Could not resolve message for code: {}", messageCode, e);
            return defaultMessage;
        }
    }

    /**
     * Check if task has any validation errors
     */
    public boolean hasErrors(Errors errors) {
        return errors.hasErrors();
    }

    /**
     * Get all validation error messages
     */
    public List<String> getAllErrorMessages(Errors errors) {
        List<String> messages = new ArrayList<>();
        
        if (errors.hasGlobalErrors()) {
            errors.getGlobalErrors().forEach(error -> 
                messages.add(getMessage(error.getCode(), error.getDefaultMessage())));
        }
        
        if (errors.hasFieldErrors()) {
            errors.getFieldErrors().forEach(error -> 
                messages.add(getMessage(error.getCode(), error.getDefaultMessage())));
        }
        
        return messages;
    }
} 