import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { tasksApi } from '../api/tasks';
import { projectsApi } from '../api/projects';
import { usePermissions } from '../hooks/usePermissions';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Spinner from '../components/common/Spinner';
import ErrorAlert from '../components/common/ErrorAlert';
import TaskForm from '../components/tasks/TaskForm';
import CommentList from '../components/tasks/CommentList';
import { TASK_STATUSES, type TaskStatus } from '../types';
import type { TaskValues } from '../validation/schemas';
import { formatDate, isOverdue, priorityLabel, statusLabel } from '../utils/labels';
import { parseError, type ParsedError } from '../utils/errors';

export default function TaskDetailPage() {
  const id = Number(useParams().id);
  const navigate = useNavigate();
  const qc = useQueryClient();
  const { canManageProject, canChangeStatus } = usePermissions();
  const [editing, setEditing] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [formError, setFormError] = useState<ParsedError | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const task = useQuery({ queryKey: ['task', id], queryFn: () => tasksApi.get(id), enabled: Number.isFinite(id) });
  const projectId = task.data?.projectId;
  const project = useQuery({
    queryKey: ['project', projectId],
    queryFn: () => projectsApi.get(projectId!),
    enabled: projectId !== undefined,
  });
  const members = useQuery({
    queryKey: ['members', projectId],
    queryFn: () => projectsApi.members(projectId!),
    enabled: projectId !== undefined,
  });

  const refresh = () => {
    qc.invalidateQueries({ queryKey: ['task', id] });
    qc.invalidateQueries({ queryKey: ['tasks'] });
    qc.invalidateQueries({ queryKey: ['dashboard'] });
  };

  const update = useMutation({
    mutationFn: (v: TaskValues) =>
      tasksApi.update(id, {
        title: v.title,
        description: v.description,
        status: v.status,
        priority: v.priority,
        assigneeId: v.assigneeId ? Number(v.assigneeId) : null,
        dueDate: v.dueDate || null,
      }),
    onSuccess: () => {
      setEditing(false);
      setFormError(null);
      refresh();
    },
    onError: (e) => setFormError(parseError(e)),
  });

  const changeStatus = useMutation({
    mutationFn: (status: TaskStatus) => tasksApi.updateStatus(id, status),
    onSuccess: () => {
      setActionError(null);
      refresh();
    },
    onError: (e) => setActionError(parseError(e).message),
  });

  const remove = useMutation({
    mutationFn: () => tasksApi.remove(id),
    onSuccess: () => {
      refresh();
      navigate(`/projects/${projectId}`, { replace: true });
    },
    onError: (e) => {
      setDeleting(false);
      setActionError(parseError(e).message);
    },
  });

  if (task.isLoading) return <Spinner />;
  if (task.error || !task.data) {
    return <ErrorAlert message={task.error ? parseError(task.error).message : 'Task not found'} />;
  }
  const t = task.data;
  const p = project.data;
  const manage = p ? canManageProject(p) : false;
  const statusAllowed = p ? canChangeStatus(t, p) : false;
  const overdue = isOverdue(t.dueDate, t.status);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <Link to={`/projects/${t.projectId}`} className="text-sm text-indigo-600 hover:underline">
            ← {t.projectName}
          </Link>
          <h1 className="mt-1 break-words text-2xl font-bold text-slate-900">{t.title}</h1>
        </div>
        {manage && (
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => setEditing(true)}>
              Edit
            </Button>
            <Button variant="danger" onClick={() => setDeleting(true)}>
              Delete
            </Button>
          </div>
        )}
      </div>

      <ErrorAlert message={actionError} />

      <dl className="grid grid-cols-2 gap-4 rounded-lg border border-slate-200 bg-white p-4 text-sm shadow-sm md:grid-cols-4">
        <div>
          <dt className="text-slate-500">Status</dt>
          <dd>
            {statusAllowed ? (
              <select
                aria-label="Task status"
                value={t.status}
                disabled={changeStatus.isPending}
                onChange={(e) => changeStatus.mutate(e.target.value as TaskStatus)}
                className="mt-1 rounded border border-slate-300 bg-white px-2 py-1 text-sm"
              >
                {TASK_STATUSES.map((s) => (
                  <option key={s} value={s}>
                    {statusLabel(s)}
                  </option>
                ))}
              </select>
            ) : (
              <span className="font-medium">{statusLabel(t.status)}</span>
            )}
          </dd>
        </div>
        <div>
          <dt className="text-slate-500">Priority</dt>
          <dd className="font-medium">{priorityLabel(t.priority)}</dd>
        </div>
        <div>
          <dt className="text-slate-500">Assignee</dt>
          <dd className="font-medium">{t.assignee ? t.assignee.name : 'Unassigned'}</dd>
        </div>
        <div>
          <dt className="text-slate-500">Due</dt>
          <dd className={overdue ? 'font-medium text-red-600' : 'font-medium'}>
            {formatDate(t.dueDate)}
            {overdue && ' (overdue)'}
          </dd>
        </div>
        <div className="col-span-2 md:col-span-4">
          <dt className="text-slate-500">Reported by</dt>
          <dd className="font-medium">{t.reporter.name}</dd>
        </div>
      </dl>

      {t.description && <p className="whitespace-pre-wrap break-words text-sm text-slate-700">{t.description}</p>}

      <section aria-labelledby="comments-heading" className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
        <h2 id="comments-heading" className="mb-3 text-sm font-semibold text-slate-700">
          Comments
        </h2>
        <CommentList taskId={id} canModerate={manage} />
      </section>

      {editing && (
        <Modal title="Edit task" onClose={() => setEditing(false)}>
          <TaskForm
            task={t}
            members={members.data ?? []}
            submitLabel="Save"
            error={formError?.message}
            fieldErrors={formError?.fieldErrors}
            onSubmit={async (v) => {
              await update.mutateAsync(v).catch(() => undefined);
            }}
            onCancel={() => setEditing(false)}
          />
        </Modal>
      )}
      {deleting && (
        <ConfirmDialog
          title="Delete task"
          message="This permanently deletes the task and its comments."
          loading={remove.isPending}
          onConfirm={() => remove.mutate()}
          onCancel={() => setDeleting(false)}
        />
      )}
    </div>
  );
}
