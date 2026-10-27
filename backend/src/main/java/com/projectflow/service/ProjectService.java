package com.projectflow.service;

import com.projectflow.domain.Project;
import com.projectflow.domain.ProjectMember;
import com.projectflow.domain.ProjectStatus;
import com.projectflow.domain.Role;
import com.projectflow.domain.User;
import com.projectflow.dto.project.AddMemberRequest;
import com.projectflow.dto.project.ProjectRequest;
import com.projectflow.dto.project.ProjectResponse;
import com.projectflow.dto.user.UserResponse;
import com.projectflow.exception.ConflictException;
import com.projectflow.exception.ForbiddenException;
import com.projectflow.exception.ResourceNotFoundException;
import com.projectflow.mapper.DtoMapper;
import com.projectflow.repository.ProjectMemberRepository;
import com.projectflow.repository.ProjectRepository;
import com.projectflow.repository.TaskRepository;
import com.projectflow.repository.UserRepository;
import com.projectflow.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Project CRUD and team membership.
 * Visibility: Admin sees all projects; others see projects they own or belong to.
 * Management (edit/delete/members): Admin, or the Project Manager who owns the project.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final DtoMapper mapper;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository memberRepository,
                          UserRepository userRepository,
                          TaskRepository taskRepository,
                          DtoMapper mapper) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request, UserPrincipal principal) {
        if (principal.getRole() == Role.MEMBER) {
            throw new ForbiddenException("Only Admins and Project Managers can create projects");
        }
        User owner = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project(
                request.name().trim(),
                request.description(),
                request.status() != null ? request.status() : ProjectStatus.ACTIVE,
                request.startDate(),
                request.dueDate(),
                owner);
        project = projectRepository.save(project);
        // The owner is also a team member so membership checks stay uniform.
        memberRepository.save(new ProjectMember(project, owner));
        return mapper.toProjectResponse(project, 1);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> list(UserPrincipal principal) {
        List<Project> projects = principal.getRole() == Role.ADMIN
                ? projectRepository.findAllWithOwner()
                : projectRepository.findAllAccessibleByUser(principal.getId());
        return projects.stream()
                .map(p -> mapper.toProjectResponse(p, memberRepository.countByProjectId(p.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(Long id, UserPrincipal principal) {
        Project project = requireAccess(id, principal);
        return mapper.toProjectResponse(project, memberRepository.countByProjectId(id));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request, UserPrincipal principal) {
        Project project = requireManage(id, principal);
        project.setName(request.name().trim());
        project.setDescription(request.description());
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        project.setStartDate(request.startDate());
        project.setDueDate(request.dueDate());
        project = projectRepository.save(project);
        return mapper.toProjectResponse(project, memberRepository.countByProjectId(id));
    }

    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        Project project = requireManage(id, principal);
        // Members, tasks and comments are removed by ON DELETE CASCADE in the schema.
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listMembers(Long id, UserPrincipal principal) {
        requireAccess(id, principal);
        return memberRepository.findAllByProjectIdWithUser(id).stream()
                .map(mapper::toUserResponse)
                .toList();
    }

    @Transactional
    public UserResponse addMember(Long id, AddMemberRequest request, UserPrincipal principal) {
        Project project = requireManage(id, principal);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("No registered user with that email"));
        if (memberRepository.existsByProjectIdAndUserId(id, user.getId())) {
            throw new ConflictException("User is already a member of this project");
        }
        memberRepository.save(new ProjectMember(project, user));
        return mapper.toUserResponse(user);
    }

    @Transactional
    public void removeMember(Long id, Long userId, UserPrincipal principal) {
        Project project = requireManage(id, principal);
        if (project.getOwner().getId().equals(userId)) {
            throw new ConflictException("The project owner cannot be removed from the project");
        }
        if (!memberRepository.existsByProjectIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("User is not a member of this project");
        }
        // A removed member must not keep tasks assigned to them.
        taskRepository.unassignUserFromProject(id, userId);
        memberRepository.deleteByProjectIdAndUserId(id, userId);
    }

    /** Loads the project if the caller may view it. Non-members get 404 so project ids are not leaked. */
    @Transactional(readOnly = true)
    public Project requireAccess(Long id, UserPrincipal principal) {
        Project project = projectRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!canAccess(project, principal)) {
            throw new ResourceNotFoundException("Project not found");
        }
        return project;
    }

    /** Loads the project if the caller may manage it (Admin, or the owning Project Manager). */
    @Transactional(readOnly = true)
    public Project requireManage(Long id, UserPrincipal principal) {
        Project project = requireAccess(id, principal);
        if (!canManage(project, principal)) {
            throw new ForbiddenException("You do not have permission to manage this project");
        }
        return project;
    }

    public boolean canManage(Project project, UserPrincipal principal) {
        if (principal.getRole() == Role.ADMIN) {
            return true;
        }
        return principal.getRole() == Role.PROJECT_MANAGER
                && project.getOwner().getId().equals(principal.getId());
    }

    private boolean canAccess(Project project, UserPrincipal principal) {
        return principal.getRole() == Role.ADMIN
                || project.getOwner().getId().equals(principal.getId())
                || memberRepository.existsByProjectIdAndUserId(project.getId(), principal.getId());
    }
}
