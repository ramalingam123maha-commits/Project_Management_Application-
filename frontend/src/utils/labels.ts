import type { ProjectStatus, Role, TaskPriority, TaskStatus } from '../types';

export const statusLabel = (s: TaskStatus): string =>
  ({ TODO: 'To do', IN_PROGRESS: 'In progress', IN_REVIEW: 'In review', DONE: 'Done' })[s];

export const priorityLabel = (p: TaskPriority): string =>
  ({ LOW: 'Low', MEDIUM: 'Medium', HIGH: 'High', CRITICAL: 'Critical' })[p];

export const projectStatusLabel = (s: ProjectStatus): string =>
  ({ ACTIVE: 'Active', ON_HOLD: 'On hold', COMPLETED: 'Completed' })[s];

export const roleLabel = (r: Role): string =>
  ({ ADMIN: 'Admin', PROJECT_MANAGER: 'Project Manager', MEMBER: 'Member' })[r];

export const formatDate = (d: string | null | undefined): string =>
  d ? new Date(d + (d.length === 10 ? 'T00:00:00' : '')).toLocaleDateString() : '—';

export const isOverdue = (dueDate: string | null, status: TaskStatus): boolean =>
  !!dueDate && status !== 'DONE' && dueDate < new Date().toISOString().slice(0, 10);
