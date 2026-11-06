import { useEffect, useRef, useState } from 'react';
import { ChatMessage } from './ChatMessage';
import { useSendMessage, useChatSession } from '../hooks/useChat';
import { ChatMessage as IChatMessage } from '../api/chat';
import { Loader } from 'lucide-react';

interface ChatWindowProps {
  sessionId: number | null;
}

export function ChatWindow({ sessionId }: ChatWindowProps) {
  const { session, isLoading: isSessionLoading } = useChatSession(sessionId);
  const { sendMessage, isPending, messages, setMessages } = useSendMessage(sessionId);
  const [inputValue, setInputValue] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const [allMessages, setAllMessages] = useState<IChatMessage[]>([]);

  // Update messages when session loads or local messages change
  useEffect(() => {
    if (session?.messages) {
      setAllMessages(session.messages);
    }
  }, [session]);

  // Add new messages to the display
  useEffect(() => {
    if (messages.length > 0) {
      setAllMessages((prev) => [...prev, ...messages]);
    }
  }, [messages]);

  // Scroll to bottom when messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [allMessages]);

  const handleSendMessage = () => {
    if (!inputValue.trim() || !sessionId || isPending) return;

    // Add user message optimistically
    const userMessage: IChatMessage = {
      id: Date.now(),
      role: 'USER',
      content: inputValue,
      createdAt: new Date().toISOString(),
    };
    setAllMessages((prev) => [...prev, userMessage]);

    sendMessage({
      content: inputValue,
    });

    setInputValue('');
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  if (!sessionId) {
    return (
      <div className="flex items-center justify-center h-full text-gray-500">
        <p>Select a chat session to start messaging</p>
      </div>
    );
  }

  if (isSessionLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <Loader className="animate-spin" />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full bg-white">
      {/* Messages Area */}
      <div className="flex-1 overflow-y-auto p-4 bg-gray-50">
        {allMessages.length === 0 ? (
          <div className="flex items-center justify-center h-full text-gray-500">
            <p>Start a conversation</p>
          </div>
        ) : (
          <>
            {allMessages.map((message) => (
              <ChatMessage key={message.id} message={message} />
            ))}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      {/* Input Area */}
      <div className="border-t border-gray-200 p-4 bg-white">
        <div className="flex gap-2">
          <textarea
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Type your message... (Shift+Enter for new line)"
            className="flex-1 p-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
            rows={3}
            disabled={isPending}
          />
          <button
            onClick={handleSendMessage}
            disabled={!inputValue.trim() || isPending}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors self-end"
          >
            {isPending ? <Loader className="animate-spin" size={20} /> : 'Send'}
          </button>
        </div>
      </div>
    </div>
  );
}
