package com.projectflow.controller;

import com.projectflow.dto.chat.ChatMessageRequest;
import com.projectflow.dto.chat.ChatMessageResponse;
import com.projectflow.dto.chat.ChatSessionResponse;
import com.projectflow.security.UserPrincipal;
import com.projectflow.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Create a new chat session
     */
    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionResponse> createSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String title) {
        ChatSessionResponse session = chatService.createSession(
                principal.getId(),
                title != null ? title : "New Chat"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    /**
     * Get all chat sessions for the current user
     */
    @GetMapping("/sessions")
    public ResponseEntity<Page<ChatSessionResponse>> getUserSessions(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        Page<ChatSessionResponse> sessions = chatService.getUserSessions(principal.getId(), pageable);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get a specific chat session
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ChatSessionResponse> getSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserPrincipal principal) {
        ChatSessionResponse session = chatService.getSession(sessionId, principal.getId());
        return ResponseEntity.ok(session);
    }

    /**
     * Get all messages in a session
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getSessionMessages(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ChatMessageResponse> messages = chatService.getSessionMessages(sessionId, principal.getId());
        return ResponseEntity.ok(messages);
    }

    /**
     * Send a message and get an AI response
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long sessionId,
            @Valid @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ChatMessageResponse response = chatService.addMessage(sessionId, principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Delete a chat session
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserPrincipal principal) {
        chatService.deleteSession(sessionId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
