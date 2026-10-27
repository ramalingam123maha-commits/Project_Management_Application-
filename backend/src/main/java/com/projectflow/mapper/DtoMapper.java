package com.projectflow.mapper;

import com.projectflow.domain.Project;
import com.projectflow.domain.ProjectMember;
import com.projectflow.domain.Task;
import com.projectflow.domain.TaskComment;
import com.projectflow.domain.User;
import com.projectflow.dto.project.ProjectResponse;
import com.projectflow.dto.task.CommentResponse;
import com.projectflow.dto.task.TaskResponse;
import com.projectflow.dto.user.UserResponse;
import org.springframework.stereotype.Component;

/**
 * Entity to DTO conversion. Never exposes the password hash.
 * Callers should load associations (JOIN FETCH) before mapping.
 */
@Component
public class DtoMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    public UserResponse toUserResponse(ProjectMember member) {
        return member == null ? null : toUserResponse(member.getUser());
    }

    public ProjectResponse toProjectResponse(Project project, long memberCount) {
        if (project == null) {
            return null;
        }
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getStartDate(),
                project.getDueDate(),
                toUserResponse(project.getOwner()),
                memberCount,
                project.getCreatedAt(),
                project.getUpdatedAt());
    }

    public TaskResponse toTaskResponse(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                toUserResponse(task.getAssignee()),
                toUserResponse(task.getReporter()),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }

    public CommentResponse toCommentResponse(TaskComment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                toUserResponse(comment.getAuthor()),
                comment.getContent(),
                comment.getCreatedAt());
    }
}
