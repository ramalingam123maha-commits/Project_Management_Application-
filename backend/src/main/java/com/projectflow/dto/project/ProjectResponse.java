package com.projectflow.dto.project;

import com.projectflow.domain.ProjectStatus;
import com.projectflow.dto.user.UserResponse;

import java.time.Instant;
import java.time.LocalDate;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate dueDate,
        UserResponse owner,
        long memberCount,
        Instant createdAt,
        Instant updatedAt
) {
}
