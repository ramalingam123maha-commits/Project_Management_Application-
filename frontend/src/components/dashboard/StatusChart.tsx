import { TASK_STATUSES, type TaskStatus } from '../../types';
import { statusLabel } from '../../utils/labels';

const colours: Record<TaskStatus, string> = {
  TODO: 'bg-slate-400',
  IN_PROGRESS: 'bg-blue-500',
  IN_REVIEW: 'bg-amber-500',
  DONE: 'bg-green-500',
};

/** Horizontal bars. The counts are also written out so colour is not the only signal. */
export default function StatusChart({ data }: { data: Record<TaskStatus, number> }) {
  const max = Math.max(1, ...TASK_STATUSES.map((s) => data[s] ?? 0));
  return (
    <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h2 className="mb-3 text-sm font-semibold text-slate-700">Tasks by status</h2>
      <ul className="space-y-3">
        {TASK_STATUSES.map((s) => {
          const count = data[s] ?? 0;
          return (
            <li key={s}>
              <div className="mb-1 flex justify-between text-sm">
                <span>{statusLabel(s)}</span>
                <span className="font-medium">{count}</span>
              </div>
              <div className="h-2 rounded bg-slate-100" role="presentation">
                <div className={`h-2 rounded ${colours[s]}`} style={{ width: `${(count / max) * 100}%` }} />
              </div>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
