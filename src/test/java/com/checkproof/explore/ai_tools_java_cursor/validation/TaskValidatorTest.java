package com.checkproof.explore.ai_tools_java_cursor.validation;

import com.checkproof.explore.ai_tools_java_cursor.dto.TaskDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.ParticipantDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.RecurrencePatternDto;
import com.checkproof.explore.ai_tools_java_cursor.exception.InvalidTaskException;
import com.checkproof.explore.ai_tools_java_cursor.model.Task;
import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskValidator Tests")
class TaskValidatorTest {

    @Mock
    private MessageSource messageSource;

    private TaskValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TaskValidator(messageSource);
        lenient().when(messageSource.getMessage(anyString(), isNull(), anyString(), any())).thenAnswer(invocation -> invocation.getArgument(2));
    }

    @Test
    @DisplayName("Should support TaskDto class")
    void shouldSupportTaskDtoClass() {
        // When
        boolean result = validator.supports(TaskDto.class);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should not support non-TaskDto class")
    void shouldNotSupportNonTaskDtoClass() {
        // When
        boolean result = validator.supports(String.class);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate valid task")
    void shouldValidateValidTask() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Should reject task with null title")
    void shouldRejectTaskWithNullTitle() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setTitle(null);
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("title"));
    }

    @Test
    @DisplayName("Should reject task with empty title")
    void shouldRejectTaskWithEmptyTitle() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setTitle("   ");
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("title"));
    }

    @Test
    @DisplayName("Should reject task with title too long")
    void shouldRejectTaskWithTitleTooLong() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setTitle("a".repeat(256));
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("title"));
    }

    @Test
    @DisplayName("Should reject task with null start date")
    void shouldRejectTaskWithNullStartDate() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setStartDate(null);
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));
    }

    @Test
    @DisplayName("Should reject task with past start date")
    void shouldRejectTaskWithPastStartDate() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setStartDate(LocalDateTime.now().minusDays(1));
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));
    }

    @Test
    @DisplayName("Should reject task with end date before start date")
    void shouldRejectTaskWithEndDateBeforeStartDate() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setEndDate(taskDto.getStartDate().minusDays(1));
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("endDate"));
    }

    @Test
    @DisplayName("Should reject task with duration too long")
    void shouldRejectTaskWithDurationTooLong() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setEndDate(taskDto.getStartDate().plusYears(2));
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("endDate"));
    }

    @Test
    @DisplayName("Should reject high priority cancelled task")
    void shouldRejectHighPriorityCancelledTask() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setPriority(Task.Priority.HIGH);
        taskDto.setStatus(Task.TaskStatus.CANCELLED);
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("priority"));
    }

    @Test
    @DisplayName("Should reject completed task with urgent priority")
    void shouldRejectCompletedTaskWithUrgentPriority() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setStatus(Task.TaskStatus.COMPLETED);
        taskDto.setPriority(Task.Priority.URGENT);
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("status"));
    }

    @Test
    @DisplayName("Should reject task with invalid recurrence interval")
    void shouldRejectTaskWithInvalidRecurrenceInterval() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        RecurrencePatternDto pattern = new RecurrencePatternDto();
        pattern.setInterval(0);
        pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.DAILY);
        taskDto.setRecurrencePattern(pattern);
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("recurrencePattern.interval"));
    }

    @Test
    @DisplayName("Should reject task with recurrence interval too large")
    void shouldRejectTaskWithRecurrenceIntervalTooLarge() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        RecurrencePatternDto pattern = new RecurrencePatternDto();
        pattern.setInterval(101);
        pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.DAILY);
        taskDto.setRecurrencePattern(pattern);
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("recurrencePattern.interval"));
    }

    @Test
    @DisplayName("Should reject task scheduled on weekend")
    void shouldRejectTaskScheduledOnWeekend() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        // Set to Saturday
        taskDto.setStartDate(LocalDateTime.of(2024, 1, 6, 10, 0)); // Saturday
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));
    }

    @Test
    @DisplayName("Should reject task outside business hours")
    void shouldRejectTaskOutsideBusinessHours() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setStartDate(LocalDateTime.of(2024, 1, 15, 19, 0)); // 7 PM
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));
    }

    @Test
    @DisplayName("Should reject task with too many participants")
    void shouldRejectTaskWithTooManyParticipants() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setParticipants(createParticipantsSet(11)); // More than 10
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("participants"));
    }

    @Test
    @DisplayName("Should validate task for creation successfully")
    void shouldValidateTaskForCreationSuccessfully() {
        // Given
        TaskDto taskDto = createValidTaskDto();

        // When & Then
        assertDoesNotThrow(() -> validator.validateForCreation(taskDto));
    }

    @Test
    @DisplayName("Should throw exception when validating invalid task for creation")
    void shouldThrowExceptionWhenValidatingInvalidTaskForCreation() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setTitle(null);

        // When & Then
        InvalidTaskException exception = assertThrows(InvalidTaskException.class, 
            () -> validator.validateForCreation(taskDto));
        assertTrue(exception.getMessage().contains("Task validation failed"));
    }

    @Test
    @DisplayName("Should validate task for update successfully")
    void shouldValidateTaskForUpdateSuccessfully() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        Long taskId = 1L;

        // When & Then
        assertDoesNotThrow(() -> validator.validateForUpdate(taskDto, taskId));
    }

    @Test
    @DisplayName("Should throw exception when validating task for update without ID")
    void shouldThrowExceptionWhenValidatingTaskForUpdateWithoutId() {
        // Given
        TaskDto taskDto = createValidTaskDto();

        // When & Then
        InvalidTaskException exception = assertThrows(InvalidTaskException.class, 
            () -> validator.validateForUpdate(taskDto, null));
        assertTrue(exception.getMessage().contains("Task update validation failed"));
    }

    @Test
    @DisplayName("Should check if errors exist")
    void shouldCheckIfErrorsExist() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");
        errors.rejectValue("title", "error", "Title is required");

        // When
        boolean result = validator.hasErrors(errors);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should get all error messages")
    void shouldGetAllErrorMessages() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");
        errors.rejectValue("title", "error", "Title is required");
        errors.rejectValue("startDate", "error", "Start date is required");

        // When
        List<String> messages = validator.getAllErrorMessages(errors);

        // Then
        assertEquals(2, messages.size());
        assertTrue(messages.contains("Title is required"));
        assertTrue(messages.contains("Start date is required"));
    }

    @Test
    @DisplayName("Should validate task with description too long")
    void shouldValidateTaskWithDescriptionTooLong() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        taskDto.setDescription("a".repeat(1001)); // More than 1000 characters
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("description"));
    }

    @Test
    @DisplayName("Should validate task with valid recurrence pattern")
    void shouldValidateTaskWithValidRecurrencePattern() {
        // Given
        TaskDto taskDto = createValidTaskDto();
        RecurrencePatternDto pattern = new RecurrencePatternDto();
        pattern.setInterval(7);
        pattern.setRecurrenceType(RecurrencePattern.RecurrenceType.DAILY);
        taskDto.setRecurrencePattern(pattern);
        taskDto.setEndDate(taskDto.getStartDate().plusDays(10));
        Errors errors = new BeanPropertyBindingResult(taskDto, "taskDto");

        // When
        validator.validate(taskDto, errors);

        // Then
        assertFalse(errors.hasErrors());
    }

    private TaskDto createValidTaskDto() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setStartDate(LocalDateTime.now().plusDays(1));
        taskDto.setEndDate(LocalDateTime.now().plusDays(2));
        taskDto.setDescription("Test description");
        taskDto.setPriority(Task.Priority.MEDIUM);
        taskDto.setStatus(Task.TaskStatus.PENDING);
        taskDto.setParticipants(new HashSet<>());
        return taskDto;
    }

    private Set<ParticipantDto> createParticipantsSet(int count) {
        Set<ParticipantDto> participants = new HashSet<>();
        for (int i = 0; i < count; i++) {
            ParticipantDto participant = new ParticipantDto();
            participant.setId((long) i);
            participant.setName("Participant " + i);
            participants.add(participant);
        }
        return participants;
    }
} 