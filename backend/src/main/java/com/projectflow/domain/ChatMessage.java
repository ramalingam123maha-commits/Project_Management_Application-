package com.projectflow.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(name = "role", nullable = false, length = 30)
    private String role; // USER or ASSISTANT

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "context_type", length = 50)
    private String contextType; // PROJECT, TASK, or null

    @Column(name = "context_id")
    private Long contextId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public ChatMessage() {
    }

    public ChatMessage(ChatSession session, String role, String content) {
        this.session = session;
        this.role = role;
        this.content = content;
    }

    public ChatMessage(ChatSession session, String role, String content, String contextType, Long contextId) {
        this.session = session;
        this.role = role;
        this.content = content;
        this.contextType = contextType;
        this.contextId = contextId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatSession getSession() {
        return session;
    }

    public void setSession(ChatSession session) {
        this.session = session;
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
