package com.projectflow.dto.chat;

import java.time.Instant;
import java.util.List;

public class ChatSessionResponse {

    private Long id;

    private String title;

    private List<ChatMessageResponse> messages;

    private Instant createdAt;

    private Instant updatedAt;

    public ChatSessionResponse() {
    }

    public ChatSessionResponse(Long id, String title, List<ChatMessageResponse> messages, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.messages = messages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChatMessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageResponse> messages) {
        this.messages = messages;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
