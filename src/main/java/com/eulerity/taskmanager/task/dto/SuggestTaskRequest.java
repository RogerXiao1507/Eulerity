package com.eulerity.taskmanager.task.dto;

import jakarta.validation.constraints.NotBlank;

public record SuggestTaskRequest(
        @NotBlank String description
) {
}
