package com.projectflow.dto.chat;

import jakarta.validation.constraints.NotBlank;

public class ChatMessageRequest {

    @NotBlank(message = "Message content cannot be blank")
    private String content;

    private String contextType;

    private Long contextId;

    public ChatMessageRequest() {
    }

    public ChatMessageRequest(String content) {
        this.content = content;
    }

    public ChatMessageRequest(String content, String contextType, Long contextId) {
        this.content = content;
        this.contextType = contextType;
        this.contextId = contextId;
    }

    // Getters and Setters
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
}
