import { Link } from 'react-router-dom';
import { TASK_STATUSES, type Task, type TaskPriority, type TaskStatus } from '../../types';
import { formatDate, isOverdue, priorityLabel, statusLabel } from '../../utils/labels';

const priorityStyle: Record<TaskPriority, string> = {
  LOW: 'bg-slate-100 text-slate-600',
  MEDIUM: 'bg-blue-100 text-blue-700',
  HIGH: 'bg-orange-100 text-orange-700',
  CRITICAL: 'bg-red-100 text-red-700',
};

interface Props {
  task: Task;
  canChangeStatus: boolean;
  statusLoading?: boolean;
  onStatusChange: (status: TaskStatus) => void;
}

export default function TaskCard({ task, canChangeStatus, statusLoading, onStatusChange }: Props) {
  const overdue = isOverdue(task.dueDate, task.status);
  return (
    <div className="rounded-md border border-slate-200 bg-white p-3 shadow-sm">
      <Link to={`/tasks/${task.id}`} className="block text-sm font-medium text-slate-900 hover:text-indigo-600">
        {task.title}
      </Link>
      <div className="mt-2 flex flex-wrap items-center gap-2 text-xs">
        <span className={`rounded-full px-2 py-0.5 font-medium ${priorityStyle[task.priority]}`}>
          {priorityLabel(task.priority)}
        </span>
        {task.dueDate && (
          <span className={overdue ? 'font-medium text-red-600' : 'text-slate-500'}>
            {overdue ? 'Overdue · ' : 'Due '}
            {formatDate(task.dueDate)}
          </span>
        )}
      </div>
      <p className="mt-2 truncate text-xs text-slate-500">{task.assignee ? task.assignee.name : 'Unassigned'}</p>
      {canChangeStatus && (
        <select
          aria-label={`Status for ${task.title}`}
          value={task.status}
          disabled={statusLoading}
          onChange={(e) => onStatusChange(e.target.value as TaskStatus)}
          className="mt-2 w-full rounded border border-slate-300 bg-white px-2 py-1 text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          {TASK_STATUSES.map((s) => (
            <option key={s} value={s}>
              {statusLabel(s)}
            </option>
          ))}
        </select>
      )}
    </div>
  );
}
