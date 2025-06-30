package com.checkproof.explore.ai_tools_java_cursor.service;

import com.checkproof.explore.ai_tools_java_cursor.dto.TaskDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.TaskStatisticsDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginatedResponseDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginationRequestDto;
import com.checkproof.explore.ai_tools_java_cursor.model.Task;
import com.checkproof.explore.ai_tools_java_cursor.model.RecurrencePattern;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for task management operations
 */
public interface TaskService {

    // ==================== CRUD Operations ====================

    /**
     * Create a new task
     */
    TaskDto createTask(TaskDto taskDto);

    /**
     * Get task by ID
     */
    Optional<TaskDto> getTaskById(Long id);

    /**
     * Get all tasks with pagination
     */
    PaginatedResponseDto<TaskDto> getAllTasks(PaginationRequestDto paginationRequest);

    /**
     * Update an existing task
     */
    TaskDto updateTask(Long id, TaskDto taskDto);

    /**
     * Delete a task
     */
    void deleteTask(Long id);

    // ==================== Date Range Queries ====================

    /**
     * Find tasks by date range with pagination
     */
    PaginatedResponseDto<TaskDto> findTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate, PaginationRequestDto paginationRequest);

    /**
     * Find tasks by date range without pagination
     */
    List<TaskDto> findTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find upcoming tasks within next 7 days
     */
    List<TaskDto> findUpcomingTasks();

    /**
     * Find upcoming tasks with pagination
     */
    PaginatedResponseDto<TaskDto> findUpcomingTasks(PaginationRequestDto paginationRequest);

    // ==================== Recurring Task Logic ====================

    /**
     * Create recurring tasks based on recurrence pattern
     */
    List<TaskDto> createRecurringTasks(TaskDto baseTaskDto, RecurrencePattern pattern);

    // ==================== Status Management ====================

    /**
     * Update task status
     */
    TaskDto updateTaskStatus(Long taskId, Task.TaskStatus newStatus);

    /**
     * Mark task as completed
     */
    TaskDto completeTask(Long taskId);

    /**
     * Mark task as in progress
     */
    TaskDto startTask(Long taskId);

    /**
     * Cancel task
     */
    TaskDto cancelTask(Long taskId);

    /**
     * Put task on hold
     */
    TaskDto putTaskOnHold(Long taskId);

    // ==================== Additional Query Methods ====================

    /**
     * Find tasks by priority
     */
    List<TaskDto> findTasksByPriority(Task.Priority priority);

    /**
     * Find tasks by status
     */
    List<TaskDto> findTasksByStatus(Task.TaskStatus status);

    /**
     * Find overdue tasks
     */
    List<TaskDto> findOverdueTasks();

    /**
     * Find recurring tasks
     */
    List<TaskDto> findRecurringTasks();

    /**
     * Find tasks by participant
     */
    List<TaskDto> findTasksByParticipant(Long participantId);

    /**
     * Search tasks by title
     */
    List<TaskDto> searchTasksByTitle(String title);

    // ==================== Statistics ====================

    /**
     * Get task statistics
     */
    TaskStatisticsDto getTaskStatistics();
} 