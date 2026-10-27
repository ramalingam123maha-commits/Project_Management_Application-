import { api } from './client';
import type { Task, TaskComment, TaskInput, TaskStatus } from '../types';

export const tasksApi = {
  listByProject: (projectId: number, status?: TaskStatus) =>
    api.get<Task[]>(`/projects/${projectId}/tasks`, { params: status ? { status } : {} }).then((r) => r.data),
  mine: () => api.get<Task[]>('/tasks/my').then((r) => r.data),
  get: (id: number) => api.get<Task>(`/tasks/${id}`).then((r) => r.data),
  create: (projectId: number, data: TaskInput) =>
    api.post<Task>(`/projects/${projectId}/tasks`, data).then((r) => r.data),
  update: (id: number, data: TaskInput) => api.put<Task>(`/tasks/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/tasks/${id}`).then(() => undefined),
  updateStatus: (id: number, status: TaskStatus) =>
    api.patch<Task>(`/tasks/${id}/status`, { status }).then((r) => r.data),
  assign: (id: number, assigneeId?: number | null) =>
    api.patch<Task>(`/tasks/${id}/assignee`, null, { params: assigneeId ? { assigneeId } : {} }).then((r) => r.data),
  comments: (id: number) => api.get<TaskComment[]>(`/tasks/${id}/comments`).then((r) => r.data),
  addComment: (id: number, content: string) =>
    api.post<TaskComment>(`/tasks/${id}/comments`, { content }).then((r) => r.data),
  removeComment: (id: number, commentId: number) =>
    api.delete(`/tasks/${id}/comments/${commentId}`).then(() => undefined),
};
