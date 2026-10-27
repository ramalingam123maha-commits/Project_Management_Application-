export type Role = 'ADMIN' | 'PROJECT_MANAGER' | 'MEMBER';
export type ProjectStatus = 'ACTIVE' | 'ON_HOLD' | 'COMPLETED';
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'IN_REVIEW' | 'DONE';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export const TASK_STATUSES: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
export const TASK_PRIORITIES: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
export const PROJECT_STATUSES: ProjectStatus[] = ['ACTIVE', 'ON_HOLD', 'COMPLETED'];
export const ROLES: Role[] = ['ADMIN', 'PROJECT_MANAGER', 'MEMBER'];

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface Project {
  id: number;
  name: string;
  description: string | null;
  status: ProjectStatus;
  startDate: string | null;
  dueDate: string | null;
  owner: User;
  memberCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectInput {
  name: string;
  description?: string;
  status?: ProjectStatus;
  startDate?: string | null;
  dueDate?: string | null;
}

export interface Task {
  id: number;
  projectId: number;
  projectName: string;
  title: string;
  description: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  assignee: User | null;
  reporter: User;
  dueDate: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TaskInput {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  assigneeId?: number | null;
  dueDate?: string | null;
}

export interface TaskComment {
  id: number;
  taskId: number;
  author: User;
  content: string;
  createdAt: string;
}

export interface Dashboard {
  scope: Role;
  totalProjects: number;
  activeProjects: number;
  totalTasks: number;
  overdueTasks: number;
  tasksByStatus: Record<TaskStatus, number>;
  upcomingDeadlines: Task[];
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string>;
}
