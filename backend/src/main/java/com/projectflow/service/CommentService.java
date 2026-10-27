package com.projectflow.service;

import com.projectflow.domain.Task;
import com.projectflow.domain.TaskComment;
import com.projectflow.dto.task.CommentRequest;
import com.projectflow.dto.task.CommentResponse;
import com.projectflow.exception.ForbiddenException;
import com.projectflow.exception.ResourceNotFoundException;
import com.projectflow.mapper.DtoMapper;
import com.projectflow.repository.TaskCommentRepository;
import com.projectflow.repository.UserRepository;
import com.projectflow.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Comments on tasks. Anyone who can view the project can read and add comments. */
@Service
public class CommentService {

    private final TaskCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final ProjectService projectService;
    private final DtoMapper mapper;

    public CommentService(TaskCommentRepository commentRepository,
                          UserRepository userRepository,
                          TaskService taskService,
                          ProjectService projectService,
                          DtoMapper mapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.projectService = projectService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> list(Long taskId, UserPrincipal principal) {
        taskService.requireVisibleTask(taskId, principal);
        return commentRepository.findAllByTaskIdWithAuthor(taskId).stream()
                .map(mapper::toCommentResponse).toList();
    }

    @Transactional
    public CommentResponse add(Long taskId, CommentRequest request, UserPrincipal principal) {
        Task task = taskService.requireVisibleTask(taskId, principal);
        TaskComment comment = new TaskComment(task, userRepository.getReferenceById(principal.getId()),
                request.content().trim());
        TaskComment saved = commentRepository.save(comment);
        return mapper.toCommentResponse(
                commentRepository.findAllByTaskIdWithAuthor(taskId).stream()
                        .filter(c -> c.getId().equals(saved.getId()))
                        .findFirst().orElse(saved));
    }

    /** The author or someone who manages the project can delete a comment. */
    @Transactional
    public void delete(Long taskId, Long commentId, UserPrincipal principal) {
        Task task = taskService.requireVisibleTask(taskId, principal);
        TaskComment comment = commentRepository.findById(commentId)
                .filter(c -> c.getTask().getId().equals(taskId))
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        boolean isAuthor = comment.getAuthor().getId().equals(principal.getId());
        if (!isAuthor && !projectService.canManage(task.getProject(), principal)) {
            throw new ForbiddenException("You cannot delete this comment");
        }
        commentRepository.delete(comment);
    }
}
