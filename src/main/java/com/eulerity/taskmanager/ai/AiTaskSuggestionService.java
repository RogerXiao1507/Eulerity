package com.eulerity.taskmanager.ai;

import com.eulerity.taskmanager.task.dto.SuggestTaskRequest;
import com.eulerity.taskmanager.task.dto.SuggestedTaskResponse;
import org.springframework.stereotype.Service;

@Service
public class AiTaskSuggestionService {

    private final TaskAiClient taskAiClient;

    public AiTaskSuggestionService(TaskAiClient taskAiClient) {
        this.taskAiClient = taskAiClient;
    }

    public SuggestedTaskResponse suggestTask(SuggestTaskRequest request) {
        return taskAiClient.suggestTask(request.description());
    }
}
