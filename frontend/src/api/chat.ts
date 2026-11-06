import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export interface ChatMessage {
  id: number;
  role: 'USER' | 'ASSISTANT';
  content: string;
  contextType?: string | null;
  contextId?: number | null;
  createdAt: string;
}

export interface ChatSession {
  id: number;
  title: string;
  messages: ChatMessage[];
  createdAt: string;
  updatedAt: string;
}

export interface ChatSessionSummary {
  id: number;
  title: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateMessageRequest {
  content: string;
  contextType?: string | null;
  contextId?: number | null;
}

export const chatAPI = {
  /**
   * Create a new chat session
   */
  createSession: async (title?: string): Promise<ChatSession> => {
    const params = new URLSearchParams();
    if (title) {
      params.append('title', title);
    }
    const response = await axios.post<ChatSession>(
      `${API_BASE_URL}/chat/sessions`,
      {},
      { params: Object.fromEntries(params) }
    );
    return response.data;
  },

  /**
   * Get all chat sessions for the current user
   */
  getUserSessions: async (page: number = 0, size: number = 10): Promise<any> => {
    const response = await axios.get(`${API_BASE_URL}/chat/sessions`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get a specific chat session
   */
  getSession: async (sessionId: number): Promise<ChatSession> => {
    const response = await axios.get<ChatSession>(
      `${API_BASE_URL}/chat/sessions/${sessionId}`
    );
    return response.data;
  },

  /**
   * Get all messages in a session
   */
  getSessionMessages: async (sessionId: number): Promise<ChatMessage[]> => {
    const response = await axios.get<ChatMessage[]>(
      `${API_BASE_URL}/chat/sessions/${sessionId}/messages`
    );
    return response.data;
  },

  /**
   * Send a message and get a response
   */
  sendMessage: async (
    sessionId: number,
    request: CreateMessageRequest
  ): Promise<ChatMessage> => {
    const response = await axios.post<ChatMessage>(
      `${API_BASE_URL}/chat/sessions/${sessionId}/messages`,
      request
    );
    return response.data;
  },

  /**
   * Delete a chat session
   */
  deleteSession: async (sessionId: number): Promise<void> => {
    await axios.delete(`${API_BASE_URL}/chat/sessions/${sessionId}`);
  },
};
