package com.checkproof.explore.ai_tools_java_cursor.exception;

/**
 * Exception thrown when tasks overlap in time for the same participants
 */
public class TaskOverlapException extends RuntimeException {

    public TaskOverlapException(String message) {
        super(message);
    }

    public TaskOverlapException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskOverlapException(Long participantId, String timeRange) {
        super("Task overlap detected for participant ID " + participantId + " during " + timeRange);
    }
} 