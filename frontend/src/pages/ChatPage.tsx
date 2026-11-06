import { useState } from 'react';
import { ChatSidebar } from '../components/ChatSidebar';
import { ChatWindow } from '../components/ChatWindow';

export function ChatPage() {
  const [selectedSessionId, setSelectedSessionId] = useState<number | null>(null);

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <ChatSidebar
        selectedSessionId={selectedSessionId}
        onSelectSession={setSelectedSessionId}
      />

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col">
        {/* Header */}
        <div className="bg-white border-b border-gray-200 p-4">
          <h1 className="text-2xl font-bold text-gray-900">ProjectFlow Assistant</h1>
          <p className="text-sm text-gray-600">Get help with your projects and tasks</p>
        </div>

        {/* Chat Window */}
        <ChatWindow sessionId={selectedSessionId} />
      </div>
    </div>
  );
}
