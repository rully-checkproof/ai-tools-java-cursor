package com.checkproof.explore.ai_tools_java_cursor.controller;

import com.checkproof.explore.ai_tools_java_cursor.dto.PaginatedResponseDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginationRequestDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.TaskDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.TaskStatisticsDto;
import com.checkproof.explore.ai_tools_java_cursor.model.Task;
import com.checkproof.explore.ai_tools_java_cursor.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks with pagination and filtering", 
               description = "Retrieve a paginated list of tasks with optional filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaginatedResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaginatedResponseDto<TaskDto>> getAllTasks(
            @Parameter(description = "Pagination request parameters")
            @Valid PaginationRequestDto paginationRequest) {
        
        log.info("Fetching all tasks with pagination: {}", paginationRequest);
        PaginatedResponseDto<TaskDto> response = taskService.getAllTasks(paginationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific task by ID", 
               description = "Retrieve a task by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found and retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskDto> getTaskById(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id) {
        
        log.info("Fetching task with ID: {}", id);
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new task", 
               description = "Create a new task with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task data"),
        @ApiResponse(responseCode = "409", description = "Task conflicts with existing tasks"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskDto> createTask(
            @Parameter(description = "Task details", required = true)
            @Valid @RequestBody TaskDto taskDto) {
        
        log.info("Creating new task: {}", taskDto.getTitle());
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task", 
               description = "Update a task with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task data"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "409", description = "Task conflicts with existing tasks"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskDto> updateTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated task details", required = true)
            @Valid @RequestBody TaskDto taskDto) {
        
        log.info("Updating task with ID: {}", id);
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", 
               description = "Delete a task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id) {
        
        log.info("Deleting task with ID: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming tasks", 
               description = "Retrieve tasks scheduled for the next 7 days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming tasks retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaginatedResponseDto<TaskDto>> getUpcomingTasks(
            @Parameter(description = "Pagination request parameters")
            @Valid PaginationRequestDto paginationRequest) {
        
        log.info("Fetching upcoming tasks with pagination: {}", paginationRequest);
        PaginatedResponseDto<TaskDto> response = taskService.findUpcomingTasks(paginationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get tasks in date range", 
               description = "Retrieve tasks within a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks in date range retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date parameters or pagination"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaginatedResponseDto<TaskDto>> getTasksByDateRange(
            @Parameter(description = "Start date (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @Parameter(description = "Pagination request parameters")
            @Valid PaginationRequestDto paginationRequest) {
        
        log.info("Fetching tasks in date range: {} to {}", start, end);
        PaginatedResponseDto<TaskDto> response = taskService.findTasksByDateRange(start, end, paginationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks by title", 
               description = "Search for tasks by title containing the specified text")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskDto>> searchTasksByTitle(
            @Parameter(description = "Search term for task title", required = true)
            @RequestParam String title) {
        
        log.info("Searching tasks by title: {}", title);
        List<TaskDto> tasks = taskService.searchTasksByTitle(title);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", 
               description = "Retrieve tasks filtered by their status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks by status retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskDto>> getTasksByStatus(
            @Parameter(description = "Task status", required = true)
            @PathVariable Task.TaskStatus status) {
        
        log.info("Fetching tasks by status: {}", status);
        List<TaskDto> tasks = taskService.findTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority", 
               description = "Retrieve tasks filtered by their priority level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks by priority retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid priority"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskDto>> getTasksByPriority(
            @Parameter(description = "Task priority", required = true)
            @PathVariable Task.Priority priority) {
        
        log.info("Fetching tasks by priority: {}", priority);
        List<TaskDto> tasks = taskService.findTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks", 
               description = "Retrieve tasks that are past their due date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue tasks retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        log.info("Fetching overdue tasks");
        List<TaskDto> tasks = taskService.findOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get task statistics", 
               description = "Retrieve various statistics about tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskStatisticsDto> getTaskStatistics() {
        log.info("Fetching task statistics");
        TaskStatisticsDto statistics = taskService.getTaskStatistics();
        return ResponseEntity.ok(statistics);
    }

    // Additional endpoints for task status management
    @PatchMapping("/{id}/status/{status}")
    @Operation(summary = "Update task status", 
               description = "Update the status of a specific task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskDto> updateTaskStatus(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New task status", required = true)
            @PathVariable Task.TaskStatus status) {
        
        log.info("Updating task {} status to: {}", id, status);
        TaskDto updatedTask = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark task as completed", 
               description = "Mark a specific task as completed")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task marked as completed"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskDto> completeTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id) {
        
        log.info("Marking task {} as completed", id);
        TaskDto completedTask = taskService.completeTask(id);
        return ResponseEntity.ok(completedTask);
    }

    @PatchMapping("/{id}/start")
    @Operation(summary = "Start task", 
               description = "Mark a specific task as in progress")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task started successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskDto> startTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id) {
        
        log.info("Starting task {}", id);
        TaskDto startedTask = taskService.startTask(id);
        return ResponseEntity.ok(startedTask);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel task", 
               description = "Cancel a specific task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskDto> cancelTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id) {
        
        log.info("Cancelling task {}", id);
        TaskDto cancelledTask = taskService.cancelTask(id);
        return ResponseEntity.ok(cancelledTask);
    }

    @PatchMapping("/{id}/hold")
    @Operation(summary = "Put task on hold", 
               description = "Put a specific task on hold")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task put on hold successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskDto> putTaskOnHold(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id) {
        
        log.info("Putting task {} on hold", id);
        TaskDto onHoldTask = taskService.putTaskOnHold(id);
        return ResponseEntity.ok(onHoldTask);
    }
} 