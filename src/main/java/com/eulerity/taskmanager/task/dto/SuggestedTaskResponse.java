package com.eulerity.taskmanager.task.dto;

import com.eulerity.taskmanager.task.TaskPriority;
import com.eulerity.taskmanager.task.TaskStatus;
import java.time.LocalDate;

public record SuggestedTaskResponse(
        String title,
        String description,
        LocalDate dueDate,
        TaskPriority priority,
        TaskStatus status
) {
}
