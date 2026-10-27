package com.projectflow.controller;

import com.projectflow.domain.Role;
import com.projectflow.dto.user.UpdateRoleRequest;
import com.projectflow.dto.user.UserResponse;
import com.projectflow.security.UserPrincipal;
import com.projectflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list(@RequestParam(required = false) Role role) {
        return ResponseEntity.ok(userService.listUsers(role));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> roles() {
        return ResponseEntity.ok(userService.listRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateRoleRequest request,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.updateRole(id, request.role(), principal.getId()));
    }
}
