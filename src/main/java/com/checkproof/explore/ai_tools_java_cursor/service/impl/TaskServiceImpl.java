package com.checkproof.explore.ai_tools_java_cursor.service.impl;

import com.checkproof.explore.ai_tools_java_cursor.dto.TaskDto;
import com.checkproof.explore.ai_tools_java_cursor.model.Task;
import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;
import com.checkproof.explore.ai_tools_java_cursor.model.Participant;
import com.checkproof.explore.ai_tools_java_cursor.repository.TaskRepository;
import com.checkproof.explore.ai_tools_java_cursor.repository.ParticipantRepository;
import com.checkproof.explore.ai_tools_java_cursor.repository.RecurrencePatternRepository;
import com.checkproof.explore.ai_tools_java_cursor.service.TaskService;
import com.checkproof.explore.ai_tools_java_cursor.util.RecurrenceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ParticipantRepository participantRepository;
    private final RecurrencePatternRepository recurrencePatternRepository;
    private final RecurrenceCalculator recurrenceCalculator;

    // ==================== CRUD Operations ====================

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        log.info("Creating new task: {}", taskDto.getTitle());
        
        Task task = taskDto.toEntity();
        validateTask(task);
        validateNoOverlappingTasks(task);
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());
        
        return TaskDto.fromEntity(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDto> getTaskById(Long id) {
        log.debug("Fetching task by ID: {}", id);
        return taskRepository.findById(id)
                .map(TaskDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> getAllTasks(Pageable pageable) {
        log.debug("Fetching all tasks with pagination: {}", pageable);
        return taskRepository.findAll(pageable)
                .map(TaskDto::fromEntity);
    }

    @Override
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        log.info("Updating task with ID: {}", id);
        
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
        
        Task task = taskDto.toEntity();
        validateTask(task);
        validateNoOverlappingTasks(task, id);
        
        // Update fields
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStartDate(task.getStartDate());
        existingTask.setEndDate(task.getEndDate());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setRecurrencePattern(task.getRecurrencePattern());
        existingTask.setParticipants(task.getParticipants());
        
        Task updatedTask = taskRepository.save(existingTask);
        log.info("Task updated successfully: {}", updatedTask.getId());
        
        return TaskDto.fromEntity(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);
        
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with ID: " + id);
        }
        
        taskRepository.deleteById(id);
        log.info("Task deleted successfully: {}", id);
    }

    // ==================== Date Range Queries ====================

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> findTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Finding tasks by date range: {} to {} with pagination: {}", startDate, endDate, pageable);
        return taskRepository.findTasksByDateRange(startDate, endDate, pageable)
                .map(TaskDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding tasks by date range: {} to {}", startDate, endDate);
        return taskRepository.findTasksByDateRange(startDate, endDate)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findUpcomingTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        log.debug("Finding upcoming tasks between {} and {}", now, sevenDaysLater);
        return taskRepository.findUpcomingTasks(now, sevenDaysLater)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> findUpcomingTasks(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        log.debug("Finding upcoming tasks between {} and {} with pagination: {}", now, sevenDaysLater, pageable);
        return taskRepository.findUpcomingTasks(now, sevenDaysLater, pageable)
                .map(TaskDto::fromEntity);
    }

    // ==================== Recurring Task Logic ====================

    @Override
    public List<TaskDto> createRecurringTasks(TaskDto baseTaskDto, RecurrencePattern pattern) {
        log.info("Creating recurring tasks for base task: {} with pattern: {}", baseTaskDto.getTitle(), pattern.getRecurrenceType());
        
        Task baseTask = baseTaskDto.toEntity();
        List<Task> recurringTasks = generateRecurringTasks(baseTask, pattern);
        
        // Save all recurring tasks
        List<Task> savedTasks = taskRepository.saveAll(recurringTasks);
        log.info("Created {} recurring tasks", savedTasks.size());
        
        return savedTasks.stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    private List<Task> generateRecurringTasks(Task baseTask, RecurrencePattern pattern) {
        List<Task> tasks = new java.util.ArrayList<>();
        LocalDateTime currentDate = baseTask.getStartDate();
        
        // Generate recurrence dates using utility
        List<LocalDateTime> recurrenceDates = recurrenceCalculator.generateRecurrenceDates(
            currentDate, pattern, 100); // Default max 100 occurrences
        
        for (LocalDateTime startDate : recurrenceDates) {
            Task recurringTask = createTaskInstance(baseTask, startDate);
            tasks.add(recurringTask);
        }
        
        return tasks;
    }

    private Task createTaskInstance(Task baseTask, LocalDateTime startDate) {
        return Task.builder()
                .title(baseTask.getTitle())
                .description(baseTask.getDescription())
                .startDate(startDate)
                .endDate(recurrenceCalculator.calculateEndDate(startDate, baseTask.getStartDate(), baseTask.getEndDate()))
                .priority(baseTask.getPriority())
                .status(Task.TaskStatus.PENDING)
                .participants(baseTask.getParticipants())
                .build();
    }

    // ==================== Status Management ====================

    @Override
    public TaskDto updateTaskStatus(Long taskId, Task.TaskStatus newStatus) {
        log.info("Updating task status: taskId={}, newStatus={}", taskId, newStatus);
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
        
        validateStatusTransition(task.getStatus(), newStatus);
        
        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task status updated successfully: taskId={}, status={}", taskId, newStatus);
        return TaskDto.fromEntity(updatedTask);
    }

    private void validateStatusTransition(Task.TaskStatus currentStatus, Task.TaskStatus newStatus) {
        if (currentStatus == Task.TaskStatus.COMPLETED && newStatus != Task.TaskStatus.COMPLETED) {
            throw new InvalidStatusTransitionException("Cannot change status from COMPLETED to " + newStatus);
        }
        
        if (currentStatus == Task.TaskStatus.CANCELLED && newStatus != Task.TaskStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot change status from CANCELLED to " + newStatus);
        }
    }

    @Override
    public TaskDto completeTask(Long taskId) {
        return updateTaskStatus(taskId, Task.TaskStatus.COMPLETED);
    }

    @Override
    public TaskDto startTask(Long taskId) {
        return updateTaskStatus(taskId, Task.TaskStatus.IN_PROGRESS);
    }

    @Override
    public TaskDto cancelTask(Long taskId) {
        return updateTaskStatus(taskId, Task.TaskStatus.CANCELLED);
    }

    @Override
    public TaskDto putTaskOnHold(Long taskId) {
        return updateTaskStatus(taskId, Task.TaskStatus.ON_HOLD);
    }

    // ==================== Validation ====================

    private void validateTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new InvalidTaskException("Task title is required");
        }
        
        if (task.getStartDate() == null) {
            throw new InvalidTaskException("Task start date is required");
        }
        
        if (task.getEndDate() != null && task.getEndDate().isBefore(task.getStartDate())) {
            throw new InvalidTaskException("Task end date cannot be before start date");
        }
        
        if (task.getStartDate().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new InvalidTaskException("Task start date cannot be in the past");
        }
    }

    private void validateNoOverlappingTasks(Task task) {
        validateNoOverlappingTasks(task, null);
    }

    private void validateNoOverlappingTasks(Task task, Long excludeTaskId) {
        LocalDateTime startDate = task.getStartDate();
        LocalDateTime endDate = task.getEndDate() != null ? task.getEndDate() : startDate.plusHours(1);
        
        List<Task> overlappingTasks = taskRepository.findTasksByDateRange(startDate, endDate);
        
        // Filter out the current task if updating
        if (excludeTaskId != null) {
            overlappingTasks = overlappingTasks.stream()
                    .filter(t -> !t.getId().equals(excludeTaskId))
                    .collect(Collectors.toList());
        }
        
        // Check for overlapping tasks with same participants
        Set<Long> taskParticipantIds = task.getParticipants().stream()
                .map(Participant::getId)
                .collect(Collectors.toSet());
        
        boolean hasOverlap = overlappingTasks.stream()
                .anyMatch(existingTask -> {
                    Set<Long> existingParticipantIds = existingTask.getParticipants().stream()
                            .map(Participant::getId)
                            .collect(Collectors.toSet());
                    
                    // Check if there are common participants
                    boolean hasCommonParticipants = taskParticipantIds.stream()
                            .anyMatch(existingParticipantIds::contains);
                    
                    return hasCommonParticipants && !existingTask.getStatus().equals(Task.TaskStatus.CANCELLED);
                });
        
        if (hasOverlap) {
            throw new TaskOverlapException("Task overlaps with existing tasks for the same participants");
        }
    }

    // ==================== Additional Query Methods ====================

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findTasksByPriority(Task.Priority priority) {
        log.debug("Finding tasks by priority: {}", priority);
        return taskRepository.findByPriorityOrderByStartDateAsc(priority)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findTasksByStatus(Task.TaskStatus status) {
        log.debug("Finding tasks by status: {}", status);
        return taskRepository.findByStatusOrderByStartDateAsc(status)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findOverdueTasks() {
        log.debug("Finding overdue tasks");
        return taskRepository.findOverdueTasks(LocalDateTime.now())
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findRecurringTasks() {
        log.debug("Finding recurring tasks");
        return taskRepository.findRecurringTasks()
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findTasksByParticipant(Long participantId) {
        log.debug("Finding tasks by participant: {}", participantId);
        return taskRepository.findTasksByParticipantId(participantId)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> searchTasksByTitle(String title) {
        log.debug("Searching tasks by title: {}", title);
        return taskRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ==================== Statistics ====================

    @Override
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics() {
        log.debug("Getting task statistics");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        
        return new TaskStatistics(
                taskRepository.count(),
                taskRepository.countByStatus(Task.TaskStatus.PENDING),
                taskRepository.countByStatus(Task.TaskStatus.IN_PROGRESS),
                taskRepository.countByStatus(Task.TaskStatus.COMPLETED),
                taskRepository.countByStatus(Task.TaskStatus.CANCELLED),
                taskRepository.countOverdueTasks(now),
                taskRepository.countUpcomingTasks(now, sevenDaysLater)
        );
    }

    // ==================== Exception Classes ====================

    public static class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidTaskException extends RuntimeException {
        public InvalidTaskException(String message) {
            super(message);
        }
    }

    public static class TaskOverlapException extends RuntimeException {
        public TaskOverlapException(String message) {
            super(message);
        }
    }

    public static class InvalidStatusTransitionException extends RuntimeException {
        public InvalidStatusTransitionException(String message) {
            super(message);
        }
    }
} 