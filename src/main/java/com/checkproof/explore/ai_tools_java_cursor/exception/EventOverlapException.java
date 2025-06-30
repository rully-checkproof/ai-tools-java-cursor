package com.checkproof.explore.ai_tools_java_cursor.exception;

public class EventOverlapException extends RuntimeException {
    
    public EventOverlapException(String message) {
        super(message);
    }
    
    public EventOverlapException(String message, Throwable cause) {
        super(message, cause);
    }
} 