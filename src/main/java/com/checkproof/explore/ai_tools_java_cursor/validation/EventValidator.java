package com.checkproof.explore.ai_tools_java_cursor.validation;

import com.checkproof.explore.ai_tools_java_cursor.exception.InvalidEventException;
import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom validator for Event entities with event-specific validation rules
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventValidator implements Validator {

    private final MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return Event.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof Event event) {
            validateEvent(event, errors);
        }
    }

    /**
     * Validate an event with comprehensive business rules
     */
    public void validateEvent(Event event, Errors errors) {
        validateBasicFields(event, errors);
        validateEventDate(event, errors);
        validateDuration(event, errors);
        validateCategory(event, errors);
        validateParticipants(event, errors);
        validateBusinessRules(event, errors);
    }

    /**
     * Validate basic required fields
     */
    private void validateBasicFields(Event event, Errors errors) {
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            addError(errors, "name", "event.name.required", "Event name is required");
        } else if (event.getName().length() > 255) {
            addError(errors, "name", "event.name.too.long", "Event name cannot exceed 255 characters");
        }

        if (event.getEventDate() == null) {
            addError(errors, "eventDate", "event.date.required", "Event date is required");
        }

        if (event.getDescription() != null && event.getDescription().length() > 1000) {
            addError(errors, "description", "event.description.too.long", "Event description cannot exceed 1000 characters");
        }
    }

    /**
     * Validate event date logic
     */
    private void validateEventDate(Event event, Errors errors) {
        LocalDateTime eventDate = event.getEventDate();
        
        if (eventDate != null) {
            // Check if event date is in the past
            if (eventDate.isBefore(LocalDateTime.now())) {
                addError(errors, "eventDate", "event.date.past", "Event date cannot be in the past");
            }

            // Check if event is too far in the future (more than 2 years)
            if (eventDate.isAfter(LocalDateTime.now().plusYears(2))) {
                addError(errors, "eventDate", "event.date.too.far.future", "Event cannot be scheduled more than 2 years in advance");
            }

            // Check for weekend restrictions for business events
            if (event.getCategory() != null && 
                (event.getCategory() == Event.EventCategory.BUSINESS || 
                 event.getCategory() == Event.EventCategory.MEETING)) {
                
                var dayOfWeek = eventDate.getDayOfWeek();
                if (dayOfWeek.name().equals("SATURDAY") || dayOfWeek.name().equals("SUNDAY")) {
                    addError(errors, "eventDate", "event.date.weekend.business", 
                            "Business events should not be scheduled on weekends");
                }
            }
        }
    }

    /**
     * Validate event duration
     */
    private void validateDuration(Event event, Errors errors) {
        if (event.getDurationMinutes() != null) {
            // Check if duration is positive
            if (event.getDurationMinutes() <= 0) {
                addError(errors, "durationMinutes", "event.duration.positive", "Event duration must be positive");
            }

            // Check if duration is reasonable (not more than 24 hours)
            if (event.getDurationMinutes() > 1440) {
                addError(errors, "durationMinutes", "event.duration.too.long", "Event duration cannot exceed 24 hours");
            }

            // Check if duration is too short (less than 15 minutes)
            if (event.getDurationMinutes() < 15) {
                addError(errors, "durationMinutes", "event.duration.too.short", "Event duration must be at least 15 minutes");
            }

            // Check for overnight events
            if (event.getEventDate() != null && event.getDurationMinutes() != null) {
                LocalDateTime endTime = event.getEventDate().plusMinutes(event.getDurationMinutes());
                LocalDateTime startOfNextDay = event.getEventDate().toLocalDate().plusDays(1).atStartOfDay();
                
                if (endTime.isAfter(startOfNextDay)) {
                    addError(errors, "durationMinutes", "event.duration.overnight", 
                            "Events should not span overnight periods");
                }
            }
        }
    }

    /**
     * Validate event category
     */
    private void validateCategory(Event event, Errors errors) {
        if (event.getCategory() == null) {
            addError(errors, "category", "event.category.required", "Event category is required");
        } else {
            // Validate category-specific rules
            switch (event.getCategory()) {
                case CONFERENCE:
                    validateConferenceEvent(event, errors);
                    break;
                case WORKSHOP:
                    validateWorkshopEvent(event, errors);
                    break;
                case MEETING:
                    validateMeetingEvent(event, errors);
                    break;
                case SOCIAL:
                    validateSocialEvent(event, errors);
                    break;
            }
        }
    }

    /**
     * Validate conference events
     */
    private void validateConferenceEvent(Event event, Errors errors) {
        if (event.getDurationMinutes() != null && event.getDurationMinutes() < 60) {
            addError(errors, "durationMinutes", "event.conference.duration", 
                    "Conference events must be at least 1 hour long");
        }

        if (event.getParticipants() != null && event.getParticipants().size() < 3) {
            addError(errors, "participants", "event.conference.participants", 
                    "Conference events must have at least 3 participants");
        }
    }

    /**
     * Validate workshop events
     */
    private void validateWorkshopEvent(Event event, Errors errors) {
        if (event.getDurationMinutes() != null && event.getDurationMinutes() < 30) {
            addError(errors, "durationMinutes", "event.workshop.duration", 
                    "Workshop events must be at least 30 minutes long");
        }

        if (event.getParticipants() != null && event.getParticipants().size() > 50) {
            addError(errors, "participants", "event.workshop.participants", 
                    "Workshop events cannot have more than 50 participants");
        }
    }

    /**
     * Validate meeting events
     */
    private void validateMeetingEvent(Event event, Errors errors) {
        if (event.getDurationMinutes() != null && event.getDurationMinutes() > 480) {
            addError(errors, "durationMinutes", "event.meeting.duration", 
                    "Meeting events cannot exceed 8 hours");
        }

        if (event.getParticipants() != null && event.getParticipants().size() > 20) {
            addError(errors, "participants", "event.meeting.participants", 
                    "Meeting events cannot have more than 20 participants");
        }
    }

    /**
     * Validate social events
     */
    private void validateSocialEvent(Event event, Errors errors) {
        if (event.getEventDate() != null) {
            var hour = event.getEventDate().getHour();
            if (hour < 6 || hour > 23) {
                addError(errors, "eventDate", "event.social.hours", 
                        "Social events should be scheduled between 6 AM and 11 PM");
            }
        }
    }

    /**
     * Validate participants
     */
    private void validateParticipants(Event event, Errors errors) {
        if (event.getParticipants() != null) {
            // Check participant count limits
            int participantCount = event.getParticipants().size();
            
            if (participantCount > 100) {
                addError(errors, "participants", "event.participants.too.many", 
                        "Event cannot have more than 100 participants");
            }

            // Check for duplicate participants
            long uniqueParticipants = event.getParticipants().stream()
                    .map(participant -> participant.getId())
                    .distinct()
                    .count();
            
            if (uniqueParticipants != participantCount) {
                addError(errors, "participants", "event.participants.duplicate", 
                        "Event has duplicate participants");
            }
        }
    }

    /**
     * Validate business-specific rules
     */
    private void validateBusinessRules(Event event, Errors errors) {
        // Check for business hours restrictions for business events
        if (event.getCategory() != null && 
            (event.getCategory() == Event.EventCategory.BUSINESS || 
             event.getCategory() == Event.EventCategory.MEETING) &&
            event.getEventDate() != null) {
            
            var hour = event.getEventDate().getHour();
            if (hour < 8 || hour > 18) {
                addError(errors, "eventDate", "event.business.hours", 
                        "Business events should be scheduled during business hours (8 AM - 6 PM)");
            }
        }

        // Check for minimum notice period (2 hours)
        if (event.getEventDate() != null) {
            Duration timeUntilEvent = Duration.between(LocalDateTime.now(), event.getEventDate());
            if (timeUntilEvent.toHours() < 2) {
                addError(errors, "eventDate", "event.notice.period", 
                        "Events must be scheduled at least 2 hours in advance");
            }
        }

        // Check for maximum event frequency (no more than 3 events per day)
        // This would typically be validated at the service level, but included here for completeness
    }

    /**
     * Validate event for creation
     */
    public void validateForCreation(Event event) {
        List<String> errors = new ArrayList<>();
        
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            errors.add(getMessage("event.name.required", "Event name is required"));
        }
        
        if (event.getEventDate() == null) {
            errors.add(getMessage("event.date.required", "Event date is required"));
        }
        
        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now())) {
            errors.add(getMessage("event.date.past", "Event date cannot be in the past"));
        }
        
        if (event.getDurationMinutes() != null && event.getDurationMinutes() <= 0) {
            errors.add(getMessage("event.duration.positive", "Event duration must be positive"));
        }
        
        if (!errors.isEmpty()) {
            throw new InvalidEventException("Event validation failed: " + String.join(", ", errors));
        }
    }

    /**
     * Validate event for update
     */
    public void validateForUpdate(Event event, Long eventId) {
        List<String> errors = new ArrayList<>();
        
        if (eventId == null) {
            errors.add(getMessage("event.id.required", "Event ID is required for updates"));
        }
        
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            errors.add(getMessage("event.name.required", "Event name is required"));
        }
        
        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now())) {
            errors.add(getMessage("event.date.past", "Event date cannot be in the past"));
        }
        
        if (!errors.isEmpty()) {
            throw new InvalidEventException("Event update validation failed: " + String.join(", ", errors));
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
     * Check if event has any validation errors
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