package com.projectflow.dto.chat;

import java.time.Instant;

public class ChatMessageResponse {

    private Long id;

    private String role;

    private String content;

    private String contextType;

    private Long contextId;

    private Instant createdAt;

    public ChatMessageResponse() {
    }

    public ChatMessageResponse(Long id, String role, String content, String contextType, Long contextId, Instant createdAt) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.contextType = contextType;
        this.contextId = contextId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    public Long getContextId() {
        return contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
