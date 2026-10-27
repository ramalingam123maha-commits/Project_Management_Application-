package com.projectflow.service;

import com.projectflow.domain.ProjectStatus;
import com.projectflow.domain.Role;
import com.projectflow.domain.Task;
import com.projectflow.domain.TaskStatus;
import com.projectflow.dto.dashboard.DashboardResponse;
import com.projectflow.dto.task.TaskResponse;
import com.projectflow.mapper.DtoMapper;
import com.projectflow.repository.ProjectRepository;
import com.projectflow.repository.TaskRepository;
import com.projectflow.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Role-scoped dashboard statistics.
 * Admin: whole system. Project Manager: projects they own. Member: projects they belong to and tasks assigned to them.
 */
@Service
public class DashboardService {

    private static final int UPCOMING_DAYS = 7;
    private static final int UPCOMING_LIMIT = 10;

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final DtoMapper mapper;

    public DashboardService(ProjectRepository projectRepository, TaskRepository taskRepository, DtoMapper mapper) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UserPrincipal principal) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(UPCOMING_DAYS);
        Long uid = principal.getId();
        Role role = principal.getRole();

        if (role == Role.ADMIN) {
            return build("ADMIN",
                    projectRepository.count(),
                    projectRepository.countByStatus(ProjectStatus.ACTIVE),
                    taskRepository.count(),
                    taskRepository.countOverdueTotal(today, TaskStatus.DONE),
                    taskRepository::countByStatus,
                    taskRepository.findUpcomingDeadlines(today, end, TaskStatus.DONE));
        }
        if (role == Role.PROJECT_MANAGER) {
            return build("PROJECT_MANAGER",
                    projectRepository.countByOwnerId(uid),
                    projectRepository.countByOwnerIdAndStatus(uid, ProjectStatus.ACTIVE),
                    taskRepository.countByProjectOwnerId(uid),
                    taskRepository.countOverdueByProjectOwnerId(uid, today, TaskStatus.DONE),
                    s -> taskRepository.countByProjectOwnerIdAndStatus(uid, s),
                    taskRepository.findUpcomingDeadlinesByProjectOwnerId(uid, today, end, TaskStatus.DONE));
        }
        return build("MEMBER",
                projectRepository.countAccessibleByUser(uid),
                projectRepository.countAccessibleByUserAndStatus(uid, ProjectStatus.ACTIVE),
                taskRepository.countByAssigneeId(uid),
                taskRepository.countOverdueByAssigneeId(uid, today, TaskStatus.DONE),
                s -> taskRepository.countByAssigneeIdAndStatus(uid, s),
                taskRepository.findUpcomingDeadlinesByAssigneeId(uid, today, end, TaskStatus.DONE));
    }

    private DashboardResponse build(String scope, long totalProjects, long activeProjects, long totalTasks,
                                    long overdue, Function<TaskStatus, Long> countByStatus, List<Task> upcoming) {
        Map<TaskStatus, Long> byStatus = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            byStatus.put(status, countByStatus.apply(status));
        }
        List<TaskResponse> deadlines = upcoming.stream()
                .limit(UPCOMING_LIMIT)
                .map(mapper::toTaskResponse)
                .toList();
        return new DashboardResponse(scope, totalProjects, activeProjects, totalTasks, overdue, byStatus, deadlines);
    }
}
