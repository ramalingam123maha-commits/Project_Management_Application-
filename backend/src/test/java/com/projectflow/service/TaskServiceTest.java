package com.projectflow.service;

import com.projectflow.domain.Project;
import com.projectflow.domain.ProjectStatus;
import com.projectflow.domain.Role;
import com.projectflow.domain.Task;
import com.projectflow.domain.TaskStatus;
import com.projectflow.domain.User;
import com.projectflow.dto.task.TaskRequest;
import com.projectflow.exception.ConflictException;
import com.projectflow.exception.ForbiddenException;
import com.projectflow.mapper.DtoMapper;
import com.projectflow.repository.ProjectMemberRepository;
import com.projectflow.repository.TaskRepository;
import com.projectflow.repository.UserRepository;
import com.projectflow.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock UserRepository userRepository;
    @Mock ProjectMemberRepository memberRepository;
    @Mock ProjectService projectService;
    @Spy DtoMapper mapper = new DtoMapper();
    @InjectMocks TaskService taskService;

    private Project project;
    private Task task;

    private UserPrincipal principal(long id, Role role) {
        return new UserPrincipal(id, "U" + id, "u" + id + "@x.com", "h", role, Collections.emptyList());
    }

    private User user(long id, Role role) {
        User u = new User("U" + id, "u" + id + "@x.com", "h", role);
        u.setId(id);
        return u;
    }

    @BeforeEach
    void setUp() {
        project = new Project("P", null, ProjectStatus.ACTIVE, null, null, user(2L, Role.PROJECT_MANAGER));
        project.setId(10L);
        task = new Task(project, "T", null, TaskStatus.TODO, null, user(4L, Role.MEMBER), user(2L, Role.PROJECT_MANAGER), null);
        task.setId(100L);
    }

    @Test
    void assigneeCanChangeStatus() {
        when(taskRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(task));

        taskService.updateStatus(100L, TaskStatus.IN_PROGRESS, principal(4, Role.MEMBER));

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void otherMemberCannotChangeStatus() {
        when(taskRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(task));
        when(projectService.canManage(any(), any())).thenReturn(false);

        assertThrows(ForbiddenException.class,
                () -> taskService.updateStatus(100L, TaskStatus.DONE, principal(5, Role.MEMBER)));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void managerCanChangeStatusOfUnassignedTask() {
        task.setAssignee(null);
        when(taskRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(task));
        when(projectService.canManage(any(), any())).thenReturn(true);

        taskService.updateStatus(100L, TaskStatus.DONE, principal(2, Role.PROJECT_MANAGER));

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void memberCannotCreateTask() {
        when(projectService.requireManage(eq(10L), any())).thenThrow(new ForbiddenException("no"));

        assertThrows(ForbiddenException.class, () -> taskService.create(10L,
                new TaskRequest("T", null, null, null, null, null), principal(4, Role.MEMBER)));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void assigneeMustBeProjectMember() {
        when(projectService.requireManage(eq(10L), any())).thenReturn(project);
        when(userRepository.getReferenceById(2L)).thenReturn(user(2L, Role.PROJECT_MANAGER));
        when(userRepository.findById(9L)).thenReturn(Optional.of(user(9L, Role.MEMBER)));
        when(memberRepository.existsByProjectIdAndUserId(10L, 9L)).thenReturn(false);

        assertThrows(ConflictException.class, () -> taskService.create(10L,
                new TaskRequest("T", null, null, null, 9L, null), principal(2, Role.PROJECT_MANAGER)));
    }
}
