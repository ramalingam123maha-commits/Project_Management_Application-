package com.projectflow.service;

import com.projectflow.domain.Project;
import com.projectflow.domain.ProjectStatus;
import com.projectflow.domain.Role;
import com.projectflow.domain.User;
import com.projectflow.dto.project.ProjectRequest;
import com.projectflow.exception.ForbiddenException;
import com.projectflow.exception.ResourceNotFoundException;
import com.projectflow.mapper.DtoMapper;
import com.projectflow.repository.ProjectMemberRepository;
import com.projectflow.repository.ProjectRepository;
import com.projectflow.repository.TaskRepository;
import com.projectflow.repository.UserRepository;
import com.projectflow.security.UserPrincipal;
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
class ProjectServiceTest {

    @Mock ProjectRepository projectRepository;
    @Mock ProjectMemberRepository memberRepository;
    @Mock UserRepository userRepository;
    @Mock TaskRepository taskRepository;
    @Spy DtoMapper mapper = new DtoMapper();
    @InjectMocks ProjectService projectService;

    private UserPrincipal principal(long id, Role role) {
        return new UserPrincipal(id, "U" + id, "u" + id + "@x.com", "h", role, Collections.emptyList());
    }

    private Project projectOwnedBy(long ownerId) {
        User owner = new User("Owner", "o@x.com", "h", Role.PROJECT_MANAGER);
        owner.setId(ownerId);
        Project p = new Project("P", null, ProjectStatus.ACTIVE, null, null, owner);
        p.setId(10L);
        return p;
    }

    @Test
    void memberCannotCreateProject() {
        ProjectRequest request = new ProjectRequest("New", null, null, null, null);
        assertThrows(ForbiddenException.class, () -> projectService.create(request, principal(1, Role.MEMBER)));
        verifyNoInteractions(projectRepository);
    }

    @Test
    void managerCreatesProjectAndOwnerBecomesMember() {
        User owner = new User("PM", "pm@x.com", "h", Role.PROJECT_MANAGER);
        owner.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        var response = projectService.create(new ProjectRequest("  New  ", "d", null, null, null),
                principal(2, Role.PROJECT_MANAGER));

        assertEquals("New", response.name());
        assertEquals(ProjectStatus.ACTIVE, response.status());
        assertEquals(1, response.memberCount());
        verify(memberRepository).save(any());
    }

    @Test
    void nonMemberGetsNotFound() {
        when(projectRepository.findByIdWithOwner(10L)).thenReturn(Optional.of(projectOwnedBy(2L)));
        when(memberRepository.existsByProjectIdAndUserId(10L, 5L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.requireAccess(10L, principal(5, Role.MEMBER)));
    }

    @Test
    void memberWithoutManageRightsIsForbidden() {
        when(projectRepository.findByIdWithOwner(10L)).thenReturn(Optional.of(projectOwnedBy(2L)));
        when(memberRepository.existsByProjectIdAndUserId(10L, 5L)).thenReturn(true);

        assertThrows(ForbiddenException.class,
                () -> projectService.requireManage(10L, principal(5, Role.MEMBER)));
    }

    @Test
    void otherManagerCannotManageButOwnerAndAdminCan() {
        Project project = projectOwnedBy(2L);
        assertFalse(projectService.canManage(project, principal(3, Role.PROJECT_MANAGER)));
        assertTrue(projectService.canManage(project, principal(2, Role.PROJECT_MANAGER)));
        assertTrue(projectService.canManage(project, principal(99, Role.ADMIN)));
        assertFalse(projectService.canManage(project, principal(2, Role.MEMBER)));
    }
}
