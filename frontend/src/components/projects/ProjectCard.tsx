import { Link } from 'react-router-dom';
import type { Project } from '../../types';
import { formatDate, projectStatusLabel } from '../../utils/labels';

const badge = {
  ACTIVE: 'bg-green-100 text-green-700',
  ON_HOLD: 'bg-amber-100 text-amber-700',
  COMPLETED: 'bg-slate-200 text-slate-700',
} as const;

export default function ProjectCard({ project }: { project: Project }) {
  return (
    <Link
      to={`/projects/${project.id}`}
      className="block rounded-lg border border-slate-200 bg-white p-4 shadow-sm transition hover:border-indigo-300 hover:shadow"
    >
      <div className="flex items-start justify-between gap-2">
        <h2 className="truncate font-semibold text-slate-900">{project.name}</h2>
        <span className={`shrink-0 rounded-full px-2 py-0.5 text-xs font-medium ${badge[project.status]}`}>
          {projectStatusLabel(project.status)}
        </span>
      </div>
      <p className="mt-1 line-clamp-2 min-h-[2.5rem] text-sm text-slate-500">
        {project.description || 'No description'}
      </p>
      <div className="mt-3 flex justify-between text-xs text-slate-500">
        <span>Owner: {project.owner.name}</span>
        <span>
          {project.memberCount} member{project.memberCount === 1 ? '' : 's'} · Due {formatDate(project.dueDate)}
        </span>
      </div>
    </Link>
  );
}
