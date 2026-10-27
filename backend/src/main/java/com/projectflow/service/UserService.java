package com.projectflow.service;

import com.projectflow.domain.Role;
import com.projectflow.domain.User;
import com.projectflow.dto.user.UserResponse;
import com.projectflow.exception.ForbiddenException;
import com.projectflow.exception.ResourceNotFoundException;
import com.projectflow.mapper.DtoMapper;
import com.projectflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DtoMapper mapper;

    public UserService(UserRepository userRepository, DtoMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers(Role roleFilter) {
        List<User> users = roleFilter == null
                ? userRepository.findAll()
                : userRepository.findByRole(roleFilter);
        return users.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(mapper::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return mapper.toUserResponse(findOrThrow(id));
    }

    @Transactional
    public UserResponse updateRole(Long targetUserId, Role newRole, Long actingAdminId) {
        if (targetUserId.equals(actingAdminId)) {
            // Prevents an admin from demoting themselves and leaving the system without an admin.
            throw new ForbiddenException("You cannot change your own role");
        }
        User user = findOrThrow(targetUserId);
        user.setRole(newRole);
        return mapper.toUserResponse(userRepository.save(user));
    }

    public List<Role> listRoles() {
        return Arrays.asList(Role.values());
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
