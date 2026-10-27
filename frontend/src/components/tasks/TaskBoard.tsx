import { TASK_STATUSES, type Task, type TaskStatus } from '../../types';
import { statusLabel } from '../../utils/labels';
import TaskCard from './TaskCard';

interface Props {
  tasks: Task[];
  canChangeStatus: (task: Task) => boolean;
  pendingTaskId?: number | null;
  onStatusChange: (task: Task, status: TaskStatus) => void;
}

/** Kanban columns. On small screens the columns scroll sideways instead of squashing. */
export default function TaskBoard({ tasks, canChangeStatus, pendingTaskId, onStatusChange }: Props) {
  return (
    <div className="-mx-4 overflow-x-auto px-4 pb-2 md:mx-0 md:px-0">
      <div className="grid min-w-[44rem] grid-cols-4 gap-3">
        {TASK_STATUSES.map((status) => {
          const column = tasks.filter((t) => t.status === status);
          return (
            <section
              key={status}
              aria-label={statusLabel(status)}
              className="rounded-lg bg-slate-100 p-2"
            >
              <h3 className="mb-2 flex items-center justify-between px-1 text-xs font-semibold uppercase tracking-wide text-slate-600">
                {statusLabel(status)}
                <span className="rounded-full bg-white px-2 py-0.5 text-slate-700">{column.length}</span>
              </h3>
              <div className="space-y-2">
                {column.map((t) => (
                  <TaskCard
                    key={t.id}
                    task={t}
                    canChangeStatus={canChangeStatus(t)}
                    statusLoading={pendingTaskId === t.id}
                    onStatusChange={(s) => onStatusChange(t, s)}
                  />
                ))}
                {column.length === 0 && <p className="px-1 py-3 text-center text-xs text-slate-400">No tasks</p>}
              </div>
            </section>
          );
        })}
      </div>
    </div>
  );
}
