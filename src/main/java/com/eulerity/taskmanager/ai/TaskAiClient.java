package com.eulerity.taskmanager.ai;

import com.eulerity.taskmanager.task.dto.SuggestedTaskResponse;

public interface TaskAiClient {

    SuggestedTaskResponse suggestTask(String description);
}
