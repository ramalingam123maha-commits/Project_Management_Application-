import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { tasksApi } from '../api/tasks';
import Spinner from '../components/common/Spinner';
import ErrorAlert from '../components/common/ErrorAlert';
import EmptyState from '../components/common/EmptyState';
import MyTaskRow from '../components/tasks/MyTaskRow';
import { TASK_STATUSES, type Task, type TaskStatus } from '../types';
import { statusLabel } from '../utils/labels';
import { parseError } from '../utils/errors';

export default function MyTasksPage() {
  const qc = useQueryClient();
  const [filter, setFilter] = useState<TaskStatus | ''>('');
  const [actionError, setActionError] = useState<string | null>(null);
  const tasks = useQuery({ queryKey: ['my-tasks'], queryFn: tasksApi.mine });

  const changeStatus = useMutation({
    mutationFn: ({ task, status }: { task: Task; status: TaskStatus }) => tasksApi.updateStatus(task.id, status),
    onSuccess: () => {
      setActionError(null);
      qc.invalidateQueries({ queryKey: ['my-tasks'] });
      qc.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (e) => setActionError(parseError(e).message),
  });

  const shown = (tasks.data ?? []).filter((t) => !filter || t.status === filter);

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-bold text-slate-900">My tasks</h1>
        <div>
          <label htmlFor="status-filter" className="sr-only">
            Filter by status
          </label>
          <select
            id="status-filter"
            value={filter}
            onChange={(e) => setFilter(e.target.value as TaskStatus | '')}
            className="rounded-md border border-slate-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">All statuses</option>
            {TASK_STATUSES.map((s) => (
              <option key={s} value={s}>
                {statusLabel(s)}
              </option>
            ))}
          </select>
        </div>
      </div>

      <ErrorAlert message={actionError ?? (tasks.error ? parseError(tasks.error).message : null)} />
      {tasks.isLoading && <Spinner />}
      {tasks.data && shown.length === 0 && <EmptyState message="No tasks match." />}
      <ul className="divide-y divide-slate-100 rounded-lg border border-slate-200 bg-white px-4 shadow-sm empty:hidden">
        {shown.map((t) => (
          <MyTaskRow
            key={t.id}
            task={t}
            disabled={changeStatus.isPending}
            onStatusChange={(status) => changeStatus.mutate({ task: t, status })}
          />
        ))}
      </ul>
    </div>
  );
}
