package com.checkproof.explore.ai_tools_java_cursor.validation;

import com.checkproof.explore.ai_tools_java_cursor.exception.InvalidEventException;
import com.checkproof.explore.ai_tools_java_cursor.model.Event;
import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventValidator Tests")
class EventValidatorTest {

    @Mock
    private MessageSource messageSource;

    private EventValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EventValidator(messageSource);
        lenient().when(messageSource.getMessage(anyString(), isNull(), anyString(), any())).thenAnswer(invocation -> invocation.getArgument(2));
    }

    @Test
    @DisplayName("Should support Event class")
    void shouldSupportEventClass() {
        // When
        boolean result = validator.supports(Event.class);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should not support non-Event class")
    void shouldNotSupportNonEventClass() {
        // When
        boolean result = validator.supports(String.class);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate valid event")
    void shouldValidateValidEvent() {
        // Given
        Event event = createValidEvent();
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Should reject event with null name")
    void shouldRejectEventWithNullName() {
        // Given
        Event event = createValidEvent();
        event.setName(null);
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    @DisplayName("Should reject event with empty name")
    void shouldRejectEventWithEmptyName() {
        // Given
        Event event = createValidEvent();
        event.setName("   ");
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    @DisplayName("Should reject event with name too long")
    void shouldRejectEventWithNameTooLong() {
        // Given
        Event event = createValidEvent();
        event.setName("a".repeat(256));
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    @DisplayName("Should reject event with null event date")
    void shouldRejectEventWithNullEventDate() {
        // Given
        Event event = createValidEvent();
        event.setEventDate(null);
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("eventDate"));
    }

    @Test
    @DisplayName("Should reject event with past date")
    void shouldRejectEventWithPastDate() {
        // Given
        Event event = createValidEvent();
        event.setEventDate(LocalDateTime.now().minusDays(1));
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("eventDate"));
    }

    @Test
    @DisplayName("Should reject event too far in future")
    void shouldRejectEventTooFarInFuture() {
        // Given
        Event event = createValidEvent();
        event.setEventDate(LocalDateTime.now().plusYears(3));
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("eventDate"));
    }

    @Test
    @DisplayName("Should reject business event on weekend")
    void shouldRejectBusinessEventOnWeekend() {
        // Given
        Event event = createValidEvent();
        event.setCategory(Event.EventCategory.BUSINESS);
        // Set to Saturday
        event.setEventDate(LocalDateTime.of(2024, 1, 6, 10, 0)); // Saturday
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("eventDate"));
    }

    @Test
    @DisplayName("Should reject event with negative duration")
    void shouldRejectEventWithNegativeDuration() {
        // Given
        Event event = createValidEvent();
        event.setDurationMinutes(-30);
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("durationMinutes"));
    }

    @Test
    @DisplayName("Should reject event with duration too long")
    void shouldRejectEventWithDurationTooLong() {
        // Given
        Event event = createValidEvent();
        event.setDurationMinutes(1500); // 25 hours
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("durationMinutes"));
    }

    @Test
    @DisplayName("Should reject event with duration too short")
    void shouldRejectEventWithDurationTooShort() {
        // Given
        Event event = createValidEvent();
        event.setDurationMinutes(10);
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("durationMinutes"));
    }

    @Test
    @DisplayName("Should reject event with null category")
    void shouldRejectEventWithNullCategory() {
        // Given
        Event event = createValidEvent();
        event.setCategory(null);
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("category"));
    }

    @Test
    @DisplayName("Should reject conference event with insufficient duration")
    void shouldRejectConferenceEventWithInsufficientDuration() {
        // Given
        Event event = createValidEvent();
        event.setCategory(Event.EventCategory.CONFERENCE);
        event.setDurationMinutes(30); // Less than 1 hour
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("durationMinutes"));
    }

    @Test
    @DisplayName("Should reject conference event with insufficient participants")
    void shouldRejectConferenceEventWithInsufficientParticipants() {
        // Given
        Event event = createValidEvent();
        event.setCategory(Event.EventCategory.CONFERENCE);
        event.setParticipants(createParticipantsSet(2)); // Less than 3
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("participants"));
    }

    @Test
    @DisplayName("Should reject workshop event with too many participants")
    void shouldRejectWorkshopEventWithTooManyParticipants() {
        // Given
        Event event = createValidEvent();
        event.setCategory(Event.EventCategory.WORKSHOP);
        event.setParticipants(createParticipantsSet(51)); // More than 50
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("participants"));
    }

    @Test
    @DisplayName("Should reject meeting event with too long duration")
    void shouldRejectMeetingEventWithTooLongDuration() {
        // Given
        Event event = createValidEvent();
        event.setCategory(Event.EventCategory.MEETING);
        event.setDurationMinutes(500); // More than 8 hours
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("durationMinutes"));
    }

    @Test
    @DisplayName("Should reject social event outside allowed hours")
    void shouldRejectSocialEventOutsideAllowedHours() {
        // Given
        Event event = createValidEvent();
        event.setCategory(Event.EventCategory.SOCIAL);
        event.setEventDate(LocalDateTime.of(2024, 1, 15, 3, 0)); // 3 AM
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("eventDate"));
    }

    @Test
    @DisplayName("Should reject event with too many participants")
    void shouldRejectEventWithTooManyParticipants() {
        // Given
        Event event = createValidEvent();
        event.setParticipants(createParticipantsSet(101)); // More than 100
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("participants"));
    }

    @Test
    @DisplayName("Should reject event with duplicate participants")
    void shouldRejectEventWithDuplicateParticipants() {
        // Given
        Event event = createValidEvent();
        Set<Participant> participants = new HashSet<>();
        Participant participant1 = new Participant();
        participant1.setId(1L);
        Participant participant2 = new Participant();
        participant2.setId(1L); // Same ID
        participants.add(participant1);
        participants.add(participant2);
        event.setParticipants(participants);
        Errors errors = new BeanPropertyBindingResult(event, "event");

        // When
        validator.validate(event, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("participants"));
    }

    @Test
    @DisplayName("Should validate event for creation successfully")
    void shouldValidateEventForCreationSuccessfully() {
        // Given
        Event event = createValidEvent();

        // When & Then
        assertDoesNotThrow(() -> validator.validateForCreation(event));
    }

    @Test
    @DisplayName("Should throw exception when validating invalid event for creation")
    void shouldThrowExceptionWhenValidatingInvalidEventForCreation() {
        // Given
        Event event = createValidEvent();
        event.setName(null);

        // When & Then
        InvalidEventException exception = assertThrows(InvalidEventException.class, 
            () -> validator.validateForCreation(event));
        assertTrue(exception.getMessage().contains("Event validation failed"));
    }

    @Test
    @DisplayName("Should validate event for update successfully")
    void shouldValidateEventForUpdateSuccessfully() {
        // Given
        Event event = createValidEvent();
        Long eventId = 1L;

        // When & Then
        assertDoesNotThrow(() -> validator.validateForUpdate(event, eventId));
    }

    @Test
    @DisplayName("Should throw exception when validating event for update without ID")
    void shouldThrowExceptionWhenValidatingEventForUpdateWithoutId() {
        // Given
        Event event = createValidEvent();

        // When & Then
        InvalidEventException exception = assertThrows(InvalidEventException.class, 
            () -> validator.validateForUpdate(event, null));
        assertTrue(exception.getMessage().contains("Event update validation failed"));
    }

    @Test
    @DisplayName("Should check if errors exist")
    void shouldCheckIfErrorsExist() {
        // Given
        Event event = createValidEvent();
        Errors errors = new BeanPropertyBindingResult(event, "event");
        errors.rejectValue("name", "error", "Name is required");

        // When
        boolean result = validator.hasErrors(errors);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should get all error messages")
    void shouldGetAllErrorMessages() {
        // Given
        Event event = createValidEvent();
        Errors errors = new BeanPropertyBindingResult(event, "event");
        errors.rejectValue("name", "error", "Name is required");
        errors.rejectValue("eventDate", "error", "Date is required");

        // When
        List<String> messages = validator.getAllErrorMessages(errors);

        // Then
        assertEquals(2, messages.size());
        assertTrue(messages.contains("Name is required"));
        assertTrue(messages.contains("Date is required"));
    }

    private Event createValidEvent() {
        Event event = new Event();
        event.setName("Test Event");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setDurationMinutes(60);
        event.setCategory(Event.EventCategory.MEETING);
        event.setDescription("Test description");
        event.setParticipants(new HashSet<>());
        return event;
    }

    private Set<Participant> createParticipantsSet(int count) {
        Set<Participant> participants = new HashSet<>();
        for (int i = 0; i < count; i++) {
            Participant participant = new Participant();
            participant.setId((long) i);
            participants.add(participant);
        }
        return participants;
    }
} 