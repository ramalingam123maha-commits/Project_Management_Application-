package com.projectflow.controller;

import com.projectflow.dto.project.AddMemberRequest;
import com.projectflow.dto.project.ProjectRequest;
import com.projectflow.dto.project.ProjectResponse;
import com.projectflow.dto.user.UserResponse;
import com.projectflow.security.UserPrincipal;
import com.projectflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request,
                                                  @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request, principal));
    }

    @GetMapping
    public List<ProjectResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return projectService.list(principal);
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return projectService.get(id, principal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ProjectResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ProjectRequest request,
                                  @AuthenticationPrincipal UserPrincipal principal) {
        return projectService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        projectService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public List<UserResponse> listMembers(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return projectService.listMembers(id, principal);
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<UserResponse> addMember(@PathVariable Long id,
                                                  @Valid @RequestBody AddMemberRequest request,
                                                  @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.addMember(id, request, principal));
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<Void> removeMember(@PathVariable Long id,
                                             @PathVariable Long userId,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        projectService.removeMember(id, userId, principal);
        return ResponseEntity.noContent().build();
    }
}
