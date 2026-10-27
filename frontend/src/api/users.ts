import { api } from './client';
import type { Role, User } from '../types';

export const usersApi = {
  list: (role?: Role) => api.get<User[]>('/users', { params: role ? { role } : {} }).then((r) => r.data),
  updateRole: (id: number, role: Role) => api.patch<User>(`/users/${id}/role`, { role }).then((r) => r.data),
};
