package com.projectflow.dto.task;

import com.projectflow.domain.TaskPriority;
import com.projectflow.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @Size(max = 5000, message = "Description must be at most 5000 characters")
        String description,

        TaskStatus status,

        TaskPriority priority,

        @Positive(message = "Assignee id must be positive")
        Long assigneeId,

        LocalDate dueDate
) {
}
