package com.checkproof.explore.ai_tools_java_cursor.exception;

/**
 * Exception thrown when a task is not found
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskNotFoundException(Long taskId) {
        super("Task not found with ID: " + taskId);
    }
} 