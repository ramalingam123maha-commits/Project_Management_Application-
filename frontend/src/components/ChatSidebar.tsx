import { useState } from 'react';
import { useChatSessions, useCreateSession, useDeleteSession } from '../hooks/useChat';
import { Plus, Trash2, Loader } from 'lucide-react';

interface ChatSidebarProps {
  selectedSessionId: number | null;
  onSelectSession: (sessionId: number) => void;
}

export function ChatSidebar({ selectedSessionId, onSelectSession }: ChatSidebarProps) {
  const { sessions, isLoading, refetch } = useChatSessions();
  const createSession = useCreateSession();
  const deleteSession = useDeleteSession();
  const [isDeleting, setIsDeleting] = useState<number | null>(null);

  const handleCreateSession = async () => {
    try {
      const newSession = await createSession.mutateAsync();
      onSelectSession(newSession.id);
      refetch();
    } catch (error) {
      console.error('Failed to create session:', error);
    }
  };

  const handleDeleteSession = async (sessionId: number) => {
    setIsDeleting(sessionId);
    try {
      await deleteSession.mutateAsync(sessionId);
      if (selectedSessionId === sessionId) {
        onSelectSession(0);
      }
      refetch();
    } catch (error) {
      console.error('Failed to delete session:', error);
    } finally {
      setIsDeleting(null);
    }
  };

  return (
    <div className="flex flex-col h-full bg-gray-900 text-white w-64">
      {/* Header */}
      <div className="p-4 border-b border-gray-700">
        <h2 className="text-lg font-bold mb-3">Chat Sessions</h2>
        <button
          onClick={handleCreateSession}
          disabled={createSession.isPending}
          className="w-full flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 rounded-lg transition-colors text-sm font-medium"
        >
          <Plus size={18} />
          New Chat
        </button>
      </div>

      {/* Sessions List */}
      <div className="flex-1 overflow-y-auto">
        {isLoading ? (
          <div className="flex items-center justify-center h-20">
            <Loader className="animate-spin" size={24} />
          </div>
        ) : sessions.length === 0 ? (
          <div className="p-4 text-center text-gray-400 text-sm">
            No chat sessions yet. Create one to get started!
          </div>
        ) : (
          <div className="space-y-2 p-2">
            {sessions.map((session: any) => (
              <div
                key={session.id}
                className={`group flex items-center justify-between p-3 rounded-lg cursor-pointer transition-colors ${
                  selectedSessionId === session.id
                    ? 'bg-blue-600'
                    : 'hover:bg-gray-800'
                }`}
                onClick={() => onSelectSession(session.id)}
              >
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium truncate">
                    {session.title || 'Untitled'}
                  </p>
                  <p className="text-xs text-gray-400">
                    {new Date(session.updatedAt).toLocaleDateString()}
                  </p>
                </div>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleDeleteSession(session.id);
                  }}
                  disabled={isDeleting === session.id}
                  className="ml-2 p-1 text-red-400 hover:text-red-300 opacity-0 group-hover:opacity-100 transition-opacity disabled:cursor-not-allowed"
                >
                  {isDeleting === session.id ? (
                    <Loader size={16} className="animate-spin" />
                  ) : (
                    <Trash2 size={16} />
                  )}
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
