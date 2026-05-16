package com.eulerity.taskmanager.task.dto;

import com.eulerity.taskmanager.task.TaskPriority;
import com.eulerity.taskmanager.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank String title,
        String description,
        LocalDate dueDate,
        @NotNull TaskPriority priority,
        @NotNull TaskStatus status
) {
}
