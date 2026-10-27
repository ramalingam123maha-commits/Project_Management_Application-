import { api } from './client';
import type { Project, ProjectInput, User } from '../types';

export const projectsApi = {
  list: () => api.get<Project[]>('/projects').then((r) => r.data),
  get: (id: number) => api.get<Project>(`/projects/${id}`).then((r) => r.data),
  create: (data: ProjectInput) => api.post<Project>('/projects', data).then((r) => r.data),
  update: (id: number, data: ProjectInput) => api.put<Project>(`/projects/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/projects/${id}`).then(() => undefined),
  members: (id: number) => api.get<User[]>(`/projects/${id}/members`).then((r) => r.data),
  addMember: (id: number, email: string) => api.post<User>(`/projects/${id}/members`, { email }).then((r) => r.data),
  removeMember: (id: number, userId: number) =>
    api.delete(`/projects/${id}/members/${userId}`).then(() => undefined),
};
