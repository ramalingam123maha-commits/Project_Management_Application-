# ProjectFlow Chat Assistant

## Overview

The Chat Assistant is an intelligent helper integrated into ProjectFlow to assist users with project and task management. It provides contextual help, guidance, and support for using the platform.

## Features

- **Multi-session Chat**: Create and manage multiple chat conversations
- **Persistent History**: All chat messages are stored and retrievable
- **Context-Aware Responses**: The assistant can provide responses based on project and task context
- **Real-time Messaging**: Instant message delivery and responses
- **User-Specific Sessions**: Each user's chat sessions are private and isolated
- **Session Management**: Create, view, and delete chat sessions

## Architecture

### Backend Components

#### Database Schema (`V2__chat_system.sql`)
- **chat_sessions**: Stores chat session metadata
  - `id`: Primary key
  - `user_id`: Foreign key to users table
  - `title`: Session title
  - `created_at`, `updated_at`: Timestamps

- **chat_messages**: Stores individual messages
  - `id`: Primary key
  - `session_id`: Foreign key to chat_sessions
  - `role`: 'USER' or 'ASSISTANT'
  - `content`: Message text
  - `context_type`: Optional context (PROJECT, TASK)
  - `context_id`: Optional context ID
  - `created_at`: Timestamp

#### Domain Models
- `ChatSession.java`: JPA entity for chat sessions
- `ChatMessage.java`: JPA entity for chat messages

#### Data Transfer Objects
- `ChatMessageRequest.java`: Inbound message request
- `ChatMessageResponse.java`: Outbound message response
- `ChatSessionResponse.java`: Session response with messages

#### Repositories
- `ChatSessionRepository.java`: CRUD and query operations for sessions
- `ChatMessageRepository.java`: CRUD and query operations for messages

#### Service Layer
- `ChatService.java`: Business logic for chat operations
  - `createSession()`: Create a new chat session
  - `getUserSessions()`: Get all sessions for a user
  - `getSession()`: Get a specific session
  - `addMessage()`: Add message and generate response
  - `getSessionMessages()`: Get all messages in a session
  - `deleteSession()`: Delete a session
  - `generateAIResponse()`: Generate contextual responses

#### Controller
- `ChatController.java`: REST API endpoints
  - `POST /api/chat/sessions`: Create session
  - `GET /api/chat/sessions`: List user sessions
  - `GET /api/chat/sessions/{sessionId}`: Get session details
  - `GET /api/chat/sessions/{sessionId}/messages`: Get messages
  - `POST /api/chat/sessions/{sessionId}/messages`: Send message
  - `DELETE /api/chat/sessions/{sessionId}`: Delete session

### Frontend Components

#### API Layer
- `api/chat.ts`: Axios API client for chat endpoints
  - `createSession()`: Create new session
  - `getUserSessions()`: Fetch user's sessions
  - `getSession()`: Fetch session details
  - `getSessionMessages()`: Fetch messages
  - `sendMessage()`: Send message and get response
  - `deleteSession()`: Delete session

#### Custom Hooks
- `hooks/useChat.ts`: React Query hooks
  - `useChatSession()`: Query session data
  - `useChatSessions()`: Query all sessions
  - `useCreateSession()`: Mutation to create session
  - `useSendMessage()`: Mutation to send message
  - `useDeleteSession()`: Mutation to delete session

#### Components
- `ChatMessage.tsx`: Individual message display
  - Differentiates user vs. assistant messages
  - Shows timestamps
  - Handles text wrapping

- `ChatWindow.tsx`: Main chat conversation area
  - Displays message history
  - Message input with auto-scroll
  - Send button with loading state
  - Supports Shift+Enter for multiline messages

- `ChatSidebar.tsx`: Session management sidebar
  - Lists all user sessions
  - Create new session button
  - Delete session functionality
  - Session selection

#### Pages
- `ChatPage.tsx`: Main chat page layout
  - Combines sidebar and chat window
  - Navigation and instructions

#### Integration
- `App.tsx`: Added `/chat` route
- `Sidebar.tsx`: Added "Chat Assistant" navigation link

## API Endpoints

### Create Session
```
POST /api/chat/sessions
Authorization: Bearer <token>
Query Parameters:
  - title: Optional session title

Response:
{
  "id": 1,
  "title": "New Chat",
  "messages": [],
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

### List Sessions
```
GET /api/chat/sessions
Authorization: Bearer <token>
Query Parameters:
  - page: Page number (default 0)
  - size: Page size (default 10)

Response:
{
  "content": [
    {
      "id": 1,
      "title": "New Chat",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  ...pagination fields...
}
```

### Get Session
```
GET /api/chat/sessions/{sessionId}
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "title": "New Chat",
  "messages": [
    {
      "id": 1,
      "role": "USER",
      "content": "How do I create a project?",
      "contextType": null,
      "contextId": null,
      "createdAt": "2024-01-01T00:00:00Z"
    },
    {
      "id": 2,
      "role": "ASSISTANT",
      "content": "To create a project...",
      "contextType": null,
      "contextId": null,
      "createdAt": "2024-01-01T00:00:01Z"
    }
  ],
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:01Z"
}
```

### Send Message
```
POST /api/chat/sessions/{sessionId}/messages
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "content": "How do I assign a task?",
  "contextType": "PROJECT",
  "contextId": 123
}

Response:
{
  "id": 3,
  "role": "ASSISTANT",
  "content": "To assign a task...",
  "contextType": null,
  "contextId": null,
  "createdAt": "2024-01-01T00:00:02Z"
}
```

### Get Session Messages
```
GET /api/chat/sessions/{sessionId}/messages
Authorization: Bearer <token>

Response: Array of ChatMessage objects
[
  {
    "id": 1,
    "role": "USER",
    "content": "Help me with tasks",
    "contextType": null,
    "contextId": null,
    "createdAt": "2024-01-01T00:00:00Z"
  },
  ...
]
```

### Delete Session
```
DELETE /api/chat/sessions/{sessionId}
Authorization: Bearer <token>

Response: 204 No Content
```

## Usage

### For Users

1. **Access Chat**: Click "Chat Assistant" in the sidebar navigation
2. **Create Session**: Click "New Chat" button to start a conversation
3. **Send Messages**: Type your question and press Enter or click Send
4. **View History**: All previous conversations are saved and can be accessed
5. **Delete Session**: Hover over a session and click the trash icon

### Supported Questions

The assistant can help with:
- Creating and managing projects
- Creating and assigning tasks
- Tracking task status and progress
- Managing team members and access
- Understanding roles and permissions
- Best practices for project management

### Example Interactions

**Q: How do I create a project?**
> A: To create a new project:
> 1. Click on 'New Project' button
> 2. Enter the project name and description
> 3. Set the start and due dates
> 4. Click 'Create'
> 
> After creating, you can invite team members to join the project.

**Q: How do I assign a task?**
> A: To assign a task to a team member:
> 1. Open the task
> 2. Click the assignee field
> 3. Select team member from the list
> 4. Save the changes

## Future Enhancements

Potential improvements for the chat system:

1. **AI Service Integration**: Connect to external AI services (OpenAI, Azure OpenAI, etc.)
2. **Smarter Responses**: Integrate with project data to provide more contextual answers
3. **Command Recognition**: Understand and execute ProjectFlow commands
4. **Multi-language Support**: Localization for different languages
5. **Chat Analytics**: Track common questions and topics
6. **Attachment Support**: Allow users to share files or screenshots
7. **Real-time Notifications**: Notify users about project updates in chat
8. **Search History**: Full-text search across chat messages
9. **Export Conversations**: Download chat history as PDF or text
10. **Team Chat**: Collaborative team conversations within projects

## Database Migration

The chat system uses Flyway for database migrations:

- **V2__chat_system.sql**: Adds chat_sessions and chat_messages tables

To deploy:
1. The migration runs automatically on application startup
2. Tables are created with proper indexes and constraints
3. Foreign key relationships ensure data integrity

## Security Considerations

- ✅ JWT authentication required for all chat endpoints
- ✅ User sessions are isolated and private
- ✅ All messages are stored server-side
- ✅ SQL injection protection via JPA
- ✅ Input validation on message content
- ✅ CORS protection on API endpoints

## Performance Optimizations

- Pagination for session lists
- Lazy loading for message histories
- Database indexes on frequently queried fields
- Transaction management for data consistency

## Error Handling

Common errors and responses:

- **401 Unauthorized**: No valid JWT token
- **404 Not Found**: Session or message doesn't exist or belongs to different user
- **400 Bad Request**: Invalid message content (blank messages)
- **500 Server Error**: Internal server error

## Dependencies Added

### Frontend
- `date-fns@^3.0.0`: Date formatting utilities
- `lucide-react@^0.263.1`: Icon library for UI elements

## Testing Recommendations

### Backend
```bash
cd backend && mvn test
```

### Frontend
```bash
cd frontend && npm test
```

### Integration Testing
1. Create a new chat session
2. Send multiple messages
3. Verify messages are saved and retrieved
4. Delete a session and verify it's removed
5. Test with different users to ensure isolation

## Troubleshooting

### Messages not appearing
- Check browser console for errors
- Verify JWT token is valid
- Ensure session ID is correct

### Can't create session
- Verify user is authenticated
- Check database connection
- Review server logs for errors

### Slow message responses
- Check server load
- Review database performance
- Consider adding message pagination

## Support

For issues or feature requests related to the chat system, please contact the development team or check the project documentation.
