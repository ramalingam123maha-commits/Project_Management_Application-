package com.projectflow.service;

import com.projectflow.domain.Project;
import com.projectflow.domain.Role;
import com.projectflow.domain.Task;
import com.projectflow.domain.TaskPriority;
import com.projectflow.domain.TaskStatus;
import com.projectflow.domain.User;
import com.projectflow.dto.task.TaskRequest;
import com.projectflow.dto.task.TaskResponse;
import com.projectflow.exception.ConflictException;
import com.projectflow.exception.ForbiddenException;
import com.projectflow.exception.ResourceNotFoundException;
import com.projectflow.mapper.DtoMapper;
import com.projectflow.repository.ProjectMemberRepository;
import com.projectflow.repository.TaskRepository;
import com.projectflow.repository.UserRepository;
import com.projectflow.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Task CRUD, assignment and status updates.
 * Create/edit/assign/delete: Admin or the owning Project Manager.
 * Status change: those two, or the task's assignee. Viewing: anyone who can view the project.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository memberRepository;
    private final ProjectService projectService;
    private final DtoMapper mapper;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       ProjectMemberRepository memberRepository,
                       ProjectService projectService,
                       DtoMapper mapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.projectService = projectService;
        this.mapper = mapper;
    }

    @Transactional
    public TaskResponse create(Long projectId, TaskRequest request, UserPrincipal principal) {
        Project project = projectService.requireManage(projectId, principal);
        User reporter = userRepository.getReferenceById(principal.getId());

        Task task = new Task();
        task.setProject(project);
        task.setReporter(reporter);
        applyFields(task, request, project);
        Task saved = taskRepository.save(task);
        return mapper.toTaskResponse(loadDetails(saved.getId()));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listByProject(Long projectId, TaskStatus status, UserPrincipal principal) {
        projectService.requireAccess(projectId, principal);
        List<Task> tasks = status == null
                ? taskRepository.findAllByProjectIdWithDetails(projectId)
                : taskRepository.findAllByProjectIdAndStatusWithDetails(projectId, status);
        return tasks.stream().map(mapper::toTaskResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listMyTasks(UserPrincipal principal) {
        return taskRepository.findAllByAssigneeIdWithDetails(principal.getId()).stream()
                .map(mapper::toTaskResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long taskId, UserPrincipal principal) {
        return mapper.toTaskResponse(requireVisibleTask(taskId, principal));
    }

    @Transactional
    public TaskResponse update(Long taskId, TaskRequest request, UserPrincipal principal) {
        Task task = requireVisibleTask(taskId, principal);
        requireManage(task, principal);
        applyFields(task, request, task.getProject());
        taskRepository.save(task);
        return mapper.toTaskResponse(loadDetails(taskId));
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, TaskStatus status, UserPrincipal principal) {
        Task task = requireVisibleTask(taskId, principal);
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(principal.getId());
        if (!isAssignee && !projectService.canManage(task.getProject(), principal)) {
            throw new ForbiddenException("Only the assignee or a project manager can change the task status");
        }
        task.setStatus(status);
        taskRepository.save(task);
        return mapper.toTaskResponse(loadDetails(taskId));
    }

    /** Assign to a project member, or pass null to unassign. */
    @Transactional
    public TaskResponse assign(Long taskId, Long assigneeId, UserPrincipal principal) {
        Task task = requireVisibleTask(taskId, principal);
        requireManage(task, principal);
        task.setAssignee(resolveAssignee(assigneeId, task.getProject()));
        taskRepository.save(task);
        return mapper.toTaskResponse(loadDetails(taskId));
    }

    @Transactional
    public void delete(Long taskId, UserPrincipal principal) {
        Task task = requireVisibleTask(taskId, principal);
        requireManage(task, principal);
        taskRepository.delete(task);
    }

    /** Loads the task if the caller can view its project; otherwise 404 so ids are not leaked. */
    @Transactional(readOnly = true)
    public Task requireVisibleTask(Long taskId, UserPrincipal principal) {
        Task task = taskRepository.findByIdWithDetails(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        projectService.requireAccess(task.getProject().getId(), principal);
        return task;
    }

    private void requireManage(Task task, UserPrincipal principal) {
        if (!projectService.canManage(task.getProject(), principal)) {
            throw new ForbiddenException("You do not have permission to manage tasks in this project");
        }
    }

    private Task loadDetails(Long taskId) {
        return taskRepository.findByIdWithDetails(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private void applyFields(Task task, TaskRequest request, Project project) {
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        if (request.status() != null) {
            task.setStatus(request.status());
        } else if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        } else if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        task.setAssignee(resolveAssignee(request.assigneeId(), project));
    }

    private User resolveAssignee(Long assigneeId, Project project) {
        if (assigneeId == null) {
            return null;
        }
        User user = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        boolean isMember = memberRepository.existsByProjectIdAndUserId(project.getId(), assigneeId)
                || project.getOwner().getId().equals(assigneeId);
        if (!isMember) {
            throw new ConflictException("Assignee must be a member of the project");
        }
        return user;
    }
}
