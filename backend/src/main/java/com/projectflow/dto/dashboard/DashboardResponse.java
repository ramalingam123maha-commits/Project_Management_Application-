package com.projectflow.dto.dashboard;

import com.projectflow.domain.TaskStatus;
import com.projectflow.dto.task.TaskResponse;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Role-scoped dashboard numbers. scope is ADMIN (whole system), PROJECT_MANAGER
 * (own projects) or MEMBER (own assigned tasks).
 */
public record DashboardResponse(
        @NotNull String scope,
        long totalProjects,
        long activeProjects,
        long totalTasks,
        long overdueTasks,
        Map<TaskStatus, Long> tasksByStatus,
        List<TaskResponse> upcomingDeadlines
) {
}
