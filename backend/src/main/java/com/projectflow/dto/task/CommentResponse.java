package com.projectflow.dto.task;

import com.projectflow.dto.user.UserResponse;

import java.time.Instant;

public record CommentResponse(Long id, Long taskId, UserResponse author, String content, Instant createdAt) {
}
