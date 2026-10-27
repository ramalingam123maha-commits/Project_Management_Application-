-- ============================================================================
-- V1__init.sql - ProjectFlow Initial Database Schema
-- Tables: users, projects, project_members, tasks, task_comments
-- Includes: Primary Keys, Foreign Keys, Unique Constraints, and Performance Indexes
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE
-- Stores user accounts, authentication credentials, and system roles.
-- Supported roles: ADMIN, PROJECT_MANAGER, MEMBER
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)        NOT NULL,
    email         VARCHAR(255)        NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    role          VARCHAR(30)         NOT NULL DEFAULT 'MEMBER',
    created_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'PROJECT_MANAGER', 'MEMBER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_users_created_at ON users (created_at);

-- ----------------------------------------------------------------------------
-- 2. PROJECTS TABLE
-- Stores project workspaces owned by an Admin or Project Manager.
-- Statuses: ACTIVE, ON_HOLD, COMPLETED
-- ----------------------------------------------------------------------------
CREATE TABLE projects (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150)        NOT NULL,
    description TEXT                NULL,
    status      VARCHAR(30)         NOT NULL DEFAULT 'ACTIVE',
    start_date  DATE                NULL,
    due_date    DATE                NULL,
    owner_id    BIGINT              NOT NULL,
    created_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_projects_owner
        FOREIGN KEY (owner_id) REFERENCES users (id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_projects_status
        CHECK (status IN ('ACTIVE', 'ON_HOLD', 'COMPLETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_projects_owner_id ON projects (owner_id);
CREATE INDEX idx_projects_status ON projects (status);
CREATE INDEX idx_projects_due_date ON projects (due_date);

-- ----------------------------------------------------------------------------
-- 3. PROJECT_MEMBERS TABLE (Join Table)
-- Represents membership of users in projects with composite primary key.
-- ----------------------------------------------------------------------------
CREATE TABLE project_members (
    project_id BIGINT    NOT NULL,
    user_id    BIGINT    NOT NULL,
    joined_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (project_id, user_id),
    CONSTRAINT fk_pm_project
        FOREIGN KEY (project_id) REFERENCES projects (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_pm_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_pm_user_id ON project_members (user_id);

-- ----------------------------------------------------------------------------
-- 4. TASKS TABLE
-- Stores tasks assigned to projects and optionally assigned to members.
-- Statuses: TODO, IN_PROGRESS, IN_REVIEW, DONE
-- Priorities: LOW, MEDIUM, HIGH, CRITICAL
-- ----------------------------------------------------------------------------
CREATE TABLE tasks (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id  BIGINT              NOT NULL,
    title       VARCHAR(200)        NOT NULL,
    description TEXT                NULL,
    status      VARCHAR(30)         NOT NULL DEFAULT 'TODO',
    priority    VARCHAR(30)         NOT NULL DEFAULT 'MEDIUM',
    assignee_id BIGINT              NULL,
    reporter_id BIGINT              NOT NULL,
    due_date    DATE                NULL,
    created_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_tasks_project
        FOREIGN KEY (project_id) REFERENCES projects (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tasks_assignee
        FOREIGN KEY (assignee_id) REFERENCES users (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_tasks_reporter
        FOREIGN KEY (reporter_id) REFERENCES users (id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_tasks_status
        CHECK (status IN ('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE')),
    CONSTRAINT chk_tasks_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tasks_project_id ON tasks (project_id);
CREATE INDEX idx_tasks_assignee_id ON tasks (assignee_id);
CREATE INDEX idx_tasks_reporter_id ON tasks (reporter_id);
CREATE INDEX idx_tasks_status ON tasks (status);
CREATE INDEX idx_tasks_priority ON tasks (priority);
CREATE INDEX idx_tasks_due_date ON tasks (due_date);
CREATE INDEX idx_tasks_project_status ON tasks (project_id, status);

-- ----------------------------------------------------------------------------
-- 5. TASK_COMMENTS TABLE
-- Stores discussion comments associated with tasks.
-- ----------------------------------------------------------------------------
CREATE TABLE task_comments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id    BIGINT    NOT NULL,
    author_id  BIGINT    NOT NULL,
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_task
        FOREIGN KEY (task_id) REFERENCES tasks (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_comments_author
        FOREIGN KEY (author_id) REFERENCES users (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_comments_task_id ON task_comments (task_id);
CREATE INDEX idx_comments_author_id ON task_comments (author_id);
CREATE INDEX idx_comments_created_at ON task_comments (created_at);
