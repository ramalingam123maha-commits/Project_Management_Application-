import { Link } from 'react-router-dom';
import { TASK_STATUSES, type Task, type TaskStatus } from '../../types';
import { formatDate, isOverdue, priorityLabel, statusLabel } from '../../utils/labels';

interface Props {
  task: Task;
  disabled?: boolean;
  onStatusChange: (status: TaskStatus) => void;
}

export default function MyTaskRow({ task, disabled, onStatusChange }: Props) {
  const overdue = isOverdue(task.dueDate, task.status);
  return (
    <li className="flex flex-col gap-2 py-3 sm:flex-row sm:items-center sm:justify-between">
      <div className="min-w-0">
        <Link to={`/tasks/${task.id}`} className="block truncate font-medium text-indigo-600 hover:underline">
          {task.title}
        </Link>
        <p className="text-xs text-slate-500">
          {task.projectName} · {priorityLabel(task.priority)} ·{' '}
          <span className={overdue ? 'font-medium text-red-600' : ''}>
            {overdue ? 'Overdue ' : 'Due '}
            {formatDate(task.dueDate)}
          </span>
        </p>
      </div>
      <select
        aria-label={`Status for ${task.title}`}
        value={task.status}
        disabled={disabled}
        onChange={(e) => onStatusChange(e.target.value as TaskStatus)}
        className="rounded-md border border-slate-300 bg-white px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
      >
        {TASK_STATUSES.map((s) => (
          <option key={s} value={s}>
            {statusLabel(s)}
          </option>
        ))}
      </select>
    </li>
  );
}
