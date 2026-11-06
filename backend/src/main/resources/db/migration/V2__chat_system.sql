-- ============================================================================
-- V2__chat_system.sql - Chat System Schema
-- Tables: chat_messages, chat_sessions
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. CHAT_SESSIONS TABLE
-- Stores chat session metadata for conversation history tracking
-- ----------------------------------------------------------------------------
CREATE TABLE chat_sessions (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT              NOT NULL,
    title         VARCHAR(255)        NULL,
    created_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_chat_sessions_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,

    INDEX idx_chat_sessions_user_id (user_id),
    INDEX idx_chat_sessions_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 2. CHAT_MESSAGES TABLE
-- Stores individual messages in a chat session
-- Role: USER or ASSISTANT
-- ============================================================================
CREATE TABLE chat_messages (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id    BIGINT              NOT NULL,
    role          VARCHAR(30)         NOT NULL,
    content       LONGTEXT            NOT NULL,
    context_type  VARCHAR(50)         NULL,
    context_id    BIGINT              NULL,
    created_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chat_messages_session
        FOREIGN KEY (session_id) REFERENCES chat_sessions (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_chat_messages_role
        CHECK (role IN ('USER', 'ASSISTANT')),
    CONSTRAINT chk_chat_messages_context_type
        CHECK (context_type IN ('PROJECT', 'TASK', NULL)),

    INDEX idx_chat_messages_session_id (session_id),
    INDEX idx_chat_messages_created_at (created_at),
    INDEX idx_chat_messages_context (context_type, context_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
