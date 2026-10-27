package com.projectflow.controller;

import com.projectflow.domain.TaskStatus;
import com.projectflow.dto.task.CommentRequest;
import com.projectflow.dto.task.CommentResponse;
import com.projectflow.dto.task.TaskRequest;
import com.projectflow.dto.task.TaskResponse;
import com.projectflow.dto.task.TaskStatusRequest;
import com.projectflow.security.UserPrincipal;
import com.projectflow.service.CommentService;
import com.projectflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;

    public TaskController(TaskService taskService, CommentService commentService) {
        this.taskService = taskService;
        this.commentService = commentService;
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> create(@PathVariable Long projectId,
                                               @Valid @RequestBody TaskRequest request,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(projectId, request, principal));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> listByProject(@PathVariable Long projectId,
                                            @RequestParam(required = false) TaskStatus status,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        return taskService.listByProject(projectId, status, principal);
    }

    @GetMapping("/tasks/my")
    public List<TaskResponse> myTasks(@AuthenticationPrincipal UserPrincipal principal) {
        return taskService.listMyTasks(principal);
    }

    @GetMapping("/tasks/{taskId}")
    public TaskResponse get(@PathVariable Long taskId, @AuthenticationPrincipal UserPrincipal principal) {
        return taskService.get(taskId, principal);
    }

    @PutMapping("/tasks/{taskId}")
    public TaskResponse update(@PathVariable Long taskId,
                               @Valid @RequestBody TaskRequest request,
                               @AuthenticationPrincipal UserPrincipal principal) {
        return taskService.update(taskId, request, principal);
    }

    @PatchMapping("/tasks/{taskId}/status")
    public TaskResponse updateStatus(@PathVariable Long taskId,
                                     @Valid @RequestBody TaskStatusRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        return taskService.updateStatus(taskId, request.status(), principal);
    }

    /** Assign to a project member by id; omit or null assigneeId to unassign. */
    @PatchMapping("/tasks/{taskId}/assignee")
    public TaskResponse assign(@PathVariable Long taskId,
                               @RequestParam(required = false) Long assigneeId,
                               @AuthenticationPrincipal UserPrincipal principal) {
        return taskService.assign(taskId, assigneeId, principal);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @AuthenticationPrincipal UserPrincipal principal) {
        taskService.delete(taskId, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<CommentResponse> listComments(@PathVariable Long taskId,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        return commentService.list(taskId, principal);
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long taskId,
                                                      @Valid @RequestBody CommentRequest request,
                                                      @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.add(taskId, request, principal));
    }

    @DeleteMapping("/tasks/{taskId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long taskId,
                                              @PathVariable Long commentId,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        commentService.delete(taskId, commentId, principal);
        return ResponseEntity.noContent().build();
    }
}
