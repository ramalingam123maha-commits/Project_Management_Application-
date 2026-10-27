package com.projectflow.dto.task;

import com.projectflow.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusRequest(
        @NotNull(message = "Status is required")
        TaskStatus status
) {
}
