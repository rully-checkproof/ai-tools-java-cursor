package com.checkproof.explore.ai_tools_java_cursor.exception;

import com.checkproof.explore.ai_tools_java_cursor.model.Task;

/**
 * Exception thrown when an invalid task status transition is attempted
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public InvalidStatusTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStatusTransitionException(Task.TaskStatus currentStatus, Task.TaskStatus newStatus) {
        super("Invalid status transition from " + currentStatus + " to " + newStatus);
    }

    public InvalidStatusTransitionException(Task.TaskStatus currentStatus, Task.TaskStatus newStatus, String reason) {
        super("Cannot change status from " + currentStatus + " to " + newStatus + ": " + reason);
    }
} 