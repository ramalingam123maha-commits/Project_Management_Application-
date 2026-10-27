import { Link } from 'react-router-dom';
import type { Task } from '../../types';
import { formatDate, priorityLabel } from '../../utils/labels';

export default function UpcomingDeadlines({ tasks }: { tasks: Task[] }) {
  return (
    <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="mb-3 text-sm font-semibold text-slate-700">Upcoming deadlines (7 days)</h2>
      {tasks.length === 0 ? (
        <p className="text-sm text-slate-500">Nothing due soon.</p>
      ) : (
        <ul className="divide-y divide-slate-100">
          {tasks.map((t) => (
            <li key={t.id} className="flex items-center justify-between gap-3 py-2 text-sm">
              <div className="min-w-0">
                <Link to={`/tasks/${t.id}`} className="block truncate font-medium text-indigo-600 hover:underline">
                  {t.title}
                </Link>
                <span className="text-xs text-slate-500">
                  {t.projectName} · {priorityLabel(t.priority)}
                </span>
              </div>
              <span className="shrink-0 text-xs text-slate-600">{formatDate(t.dueDate)}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
