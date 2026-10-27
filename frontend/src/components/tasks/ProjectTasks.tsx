import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { tasksApi } from '../../api/tasks';
import { usePermissions } from '../../hooks/usePermissions';
import Button from '../common/Button';
import Modal from '../common/Modal';
import Spinner from '../common/Spinner';
import ErrorAlert from '../common/ErrorAlert';
import TaskBoard from './TaskBoard';
import TaskForm from './TaskForm';
import type { Project, Task, TaskStatus, User } from '../../types';
import type { TaskValues } from '../../validation/schemas';
import { parseError, type ParsedError } from '../../utils/errors';

interface Props {
  project: Project;
  members: User[];
}

export default function ProjectTasks({ project, members }: Props) {
  const qc = useQueryClient();
  const { canManageProject, canChangeStatus } = usePermissions();
  const manage = canManageProject(project);
  const [creating, setCreating] = useState(false);
  const [formError, setFormError] = useState<ParsedError | null>(null);
  const [statusError, setStatusError] = useState<string | null>(null);

  const key = ['tasks', project.id];
  const tasks = useQuery({ queryKey: key, queryFn: () => tasksApi.listByProject(project.id) });

  const refresh = () => {
    qc.invalidateQueries({ queryKey: key });
    qc.invalidateQueries({ queryKey: ['dashboard'] });
  };

  const create = useMutation({
    mutationFn: (v: TaskValues) =>
      tasksApi.create(project.id, {
        title: v.title,
        description: v.description,
        status: v.status,
        priority: v.priority,
        assigneeId: v.assigneeId ? Number(v.assigneeId) : null,
        dueDate: v.dueDate || null,
      }),
    onSuccess: () => {
      setCreating(false);
      setFormError(null);
      refresh();
    },
    onError: (e) => setFormError(parseError(e)),
  });

  const changeStatus = useMutation({
    mutationFn: ({ task, status }: { task: Task; status: TaskStatus }) => tasksApi.updateStatus(task.id, status),
    onSuccess: () => {
      setStatusError(null);
      refresh();
    },
    onError: (e) => setStatusError(parseError(e).message),
  });

  return (
    <section aria-labelledby="tasks-heading" className="space-y-3">
      <div className="flex items-center justify-between gap-3">
        <h2 id="tasks-heading" className="text-lg font-semibold text-slate-900">
          Tasks
        </h2>
        {manage && <Button onClick={() => setCreating(true)}>New task</Button>}
      </div>
      <ErrorAlert message={statusError ?? (tasks.error ? parseError(tasks.error).message : null)} />
      {tasks.isLoading && <Spinner />}
      {tasks.data && (
        <TaskBoard
          tasks={tasks.data}
          canChangeStatus={(t) => canChangeStatus(t, project)}
          pendingTaskId={changeStatus.isPending ? changeStatus.variables?.task.id : null}
          onStatusChange={(task, status) => changeStatus.mutate({ task, status })}
        />
      )}
      {creating && (
        <Modal title="New task" onClose={() => setCreating(false)}>
          <TaskForm
            members={members}
            submitLabel="Create"
            error={formError?.message}
            fieldErrors={formError?.fieldErrors}
            onSubmit={async (v) => {
              await create.mutateAsync(v).catch(() => undefined);
            }}
            onCancel={() => setCreating(false)}
          />
        </Modal>
      )}
    </section>
  );
}
