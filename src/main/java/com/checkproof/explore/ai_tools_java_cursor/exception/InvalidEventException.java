package com.checkproof.explore.ai_tools_java_cursor.exception;

public class InvalidEventException extends RuntimeException {
    
    public InvalidEventException(String message) {
        super(message);
    }
    
    public InvalidEventException(String message, Throwable cause) {
        super(message, cause);
    }
} 