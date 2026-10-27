package com.projectflow.dto.task;

import com.projectflow.domain.TaskPriority;
import com.projectflow.domain.TaskStatus;
import com.projectflow.dto.user.UserResponse;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        Long projectId,
        String projectName,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        UserResponse assignee,
        UserResponse reporter,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt
) {
}
