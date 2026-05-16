package com.eulerity.taskmanager.ai;

public class AiTaskSuggestionException extends RuntimeException {

    public AiTaskSuggestionException(String message) {
        super(message);
    }

    public AiTaskSuggestionException(String message, Throwable cause) {
        super(message, cause);
    }
}
