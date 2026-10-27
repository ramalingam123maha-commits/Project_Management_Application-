import { useAuth } from '../context/AuthContext';
import type { Project, Task } from '../types';

/** UI-only hints for showing or hiding buttons. The server enforces every permission. */
export function usePermissions() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const isPm = user?.role === 'PROJECT_MANAGER';

  const canManageProject = (project: Pick<Project, 'owner'>) =>
    isAdmin || (isPm && project.owner.id === user?.id);

  const canChangeStatus = (task: Pick<Task, 'assignee'>, project: Pick<Project, 'owner'>) =>
    canManageProject(project) || task.assignee?.id === user?.id;

  return {
    isAdmin,
    canCreateProject: isAdmin || isPm,
    canManageProject,
    canChangeStatus,
  };
}
