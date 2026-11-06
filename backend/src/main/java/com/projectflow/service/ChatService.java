package com.projectflow.service;

import com.projectflow.domain.ChatMessage;
import com.projectflow.domain.ChatSession;
import com.projectflow.domain.User;
import com.projectflow.dto.chat.ChatMessageRequest;
import com.projectflow.dto.chat.ChatMessageResponse;
import com.projectflow.dto.chat.ChatSessionResponse;
import com.projectflow.exception.ResourceNotFoundException;
import com.projectflow.repository.ChatMessageRepository;
import com.projectflow.repository.ChatSessionRepository;
import com.projectflow.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatService(
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new chat session for the user
     */
    @Transactional
    public ChatSessionResponse createSession(Long userId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChatSession session = new ChatSession(user, title);
        ChatSession saved = chatSessionRepository.save(session);
        return mapToResponse(saved);
    }

    /**
     * Gets all chat sessions for a user
     */
    public Page<ChatSessionResponse> getUserSessions(Long userId, Pageable pageable) {
        return chatSessionRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets a specific chat session
     */
    public ChatSessionResponse getSession(Long sessionId, Long userId) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));
        return mapToResponse(session);
    }

    /**
     * Adds a user message to a session and generates an AI response
     */
    @Transactional
    public ChatMessageResponse addMessage(Long sessionId, Long userId, ChatMessageRequest request) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));

        // Save user message
        ChatMessage userMessage = new ChatMessage(
                session,
                "USER",
                request.getContent(),
                request.getContextType(),
                request.getContextId()
        );
        chatMessageRepository.save(userMessage);

        // Generate AI response
        String aiResponse = generateAIResponse(request.getContent(), session);

        // Save AI response
        ChatMessage aiMessage = new ChatMessage(
                session,
                "ASSISTANT",
                aiResponse
        );
        chatMessageRepository.save(aiMessage);

        // Update session title if it's the first message
        if (session.getMessages().isEmpty() && request.getContent().length() <= 100) {
            session.setTitle(request.getContent());
            chatSessionRepository.save(session);
        }

        return mapToMessageResponse(aiMessage);
    }

    /**
     * Gets all messages for a session
     */
    public List<ChatMessageResponse> getSessionMessages(Long sessionId, Long userId) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));

        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a chat session
     */
    @Transactional
    public void deleteSession(Long sessionId, Long userId) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));
        chatSessionRepository.delete(session);
    }

    /**
     * Generates an AI response for the user's message
     * This is a simple implementation that provides helpful responses about the project management app
     */
    private String generateAIResponse(String userMessage, ChatSession session) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("help") || lowerMessage.contains("?")) {
            return "I can help you with your ProjectFlow tasks! I'm here to assist with:\n" +
                    "• Creating and managing projects\n" +
                    "• Creating and assigning tasks\n" +
                    "• Tracking task status and progress\n" +
                    "• Managing team members and project access\n" +
                    "• Answering questions about your workflow\n\n" +
                    "What would you like help with today?";
        }

        if (lowerMessage.contains("project") && (lowerMessage.contains("create") || lowerMessage.contains("new"))) {
            return "To create a new project:\n" +
                    "1. Click on 'New Project' button\n" +
                    "2. Enter the project name and description\n" +
                    "3. Set the start and due dates\n" +
                    "4. Click 'Create'\n\n" +
                    "After creating, you can invite team members to join the project. Is there anything else you'd like to know?";
        }

        if (lowerMessage.contains("task") && (lowerMessage.contains("create") || lowerMessage.contains("new"))) {
            return "To create a new task:\n" +
                    "1. Navigate to your project\n" +
                    "2. Click 'New Task'\n" +
                    "3. Enter the task title and description\n" +
                    "4. Set priority (LOW, MEDIUM, HIGH, CRITICAL)\n" +
                    "5. Assign it to a team member\n" +
                    "6. Set the due date\n" +
                    "7. Click 'Create'\n\n" +
                    "Tasks can be tracked through different statuses: TODO, IN_PROGRESS, IN_REVIEW, and DONE.";
        }

        if (lowerMessage.contains("status") || lowerMessage.contains("progress")) {
            return "You can track task progress by updating the task status:\n" +
                    "• TODO: Task hasn't started yet\n" +
                    "• IN_PROGRESS: Task is currently being worked on\n" +
                    "• IN_REVIEW: Task is ready for review\n" +
                    "• DONE: Task is complete\n\n" +
                    "Only team members can change the status of tasks assigned to them. Do you want to update any specific task?";
        }

        if (lowerMessage.contains("member") || lowerMessage.contains("team") || lowerMessage.contains("access")) {
            return "To manage team members:\n" +
                    "• Project Managers can add members to their projects\n" +
                    "• Admins can promote users to different roles\n" +
                    "• Members can view projects they've been added to\n\n" +
                    "Roles available:\n" +
                    "• Admin: Full system access\n" +
                    "• Project Manager: Create projects and manage teams\n" +
                    "• Member: View projects and update assigned tasks\n\n" +
                    "Need help adding someone to your project?";
        }

        if (lowerMessage.contains("comment") || lowerMessage.contains("discussion")) {
            return "Team members can collaborate on tasks by leaving comments:\n" +
                    "1. Open the task\n" +
                    "2. Scroll to the comments section\n" +
                    "3. Type your comment\n" +
                    "4. Click 'Add Comment'\n\n" +
                    "Comments are great for discussions, updates, and sharing feedback on task progress!";
        }

        // Default helpful response
        return "I understand you're asking about: \"" + userMessage + "\"\n\n" +
                "I'm here to help with ProjectFlow! You can ask me about:\n" +
                "• How to create projects and tasks\n" +
                "• Managing team members\n" +
                "• Tracking task progress\n" +
                "• Role and permission questions\n" +
                "• Best practices for project management\n\n" +
                "Feel free to ask me anything about using ProjectFlow!";
    }

    private ChatSessionResponse mapToResponse(ChatSession session) {
        List<ChatMessageResponse> messages = chatMessageRepository
                .findBySessionIdOrderByCreatedAtAsc(session.getId())
                .stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());

        return new ChatSessionResponse(
                session.getId(),
                session.getTitle(),
                messages,
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    private ChatMessageResponse mapToMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getContextType(),
                message.getContextId(),
                message.getCreatedAt()
        );
    }
}
