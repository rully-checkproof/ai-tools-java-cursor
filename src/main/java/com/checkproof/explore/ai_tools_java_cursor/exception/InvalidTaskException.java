package com.checkproof.explore.ai_tools_java_cursor.exception;

/**
 * Exception thrown when task data is invalid
 */
public class InvalidTaskException extends RuntimeException {

    public InvalidTaskException(String message) {
        super(message);
    }

    public InvalidTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTaskException(String field, String reason) {
        super("Invalid task " + field + ": " + reason);
    }
} 