import { useState, useCallback } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import {
  chatAPI,
  ChatSession,
  ChatMessage,
  ChatSessionSummary,
  CreateMessageRequest,
} from '../api/chat';

export function useChatSession(sessionId: number | null) {
  const { data: session, isLoading, error, refetch } = useQuery({
    queryKey: ['chatSession', sessionId],
    queryFn: () => (sessionId ? chatAPI.getSession(sessionId) : Promise.reject('No session')),
    enabled: !!sessionId,
  });

  return {
    session,
    isLoading,
    error,
    refetch,
  };
}

export function useChatSessions() {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['chatSessions'],
    queryFn: () => chatAPI.getUserSessions(0, 20),
  });

  return {
    sessions: data?.content || [],
    isLoading,
    error,
    refetch,
  };
}

export function useCreateSession() {
  const mutation = useMutation({
    mutationFn: (title?: string) => chatAPI.createSession(title),
  });

  return mutation;
}

export function useSendMessage(sessionId: number | null) {
  const [messages, setMessages] = useState<ChatMessage[]>([]);

  const mutation = useMutation({
    mutationFn: (request: CreateMessageRequest) =>
      sessionId ? chatAPI.sendMessage(sessionId, request) : Promise.reject('No session'),
    onSuccess: (newMessage) => {
      setMessages((prev) => [...prev, newMessage]);
    },
  });

  return {
    sendMessage: mutation.mutate,
    isPending: mutation.isPending,
    error: mutation.error,
    messages,
    setMessages,
  };
}

export function useDeleteSession() {
  const mutation = useMutation({
    mutationFn: (sessionId: number) => chatAPI.deleteSession(sessionId),
  });

  return mutation;
}
