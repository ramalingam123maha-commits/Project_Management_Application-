import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { projectsApi } from '../api/projects';
import { usePermissions } from '../hooks/usePermissions';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Spinner from '../components/common/Spinner';
import ErrorAlert from '../components/common/ErrorAlert';
import ProjectForm from '../components/projects/ProjectForm';
import MemberList from '../components/projects/MemberList';
import AddMemberForm from '../components/projects/AddMemberForm';
import ProjectTasks from '../components/tasks/ProjectTasks';
import { formatDate, projectStatusLabel } from '../utils/labels';
import { parseError, type ParsedError } from '../utils/errors';
import type { ProjectValues } from '../validation/schemas';

export default function ProjectDetailPage() {
  const id = Number(useParams().id);
  const navigate = useNavigate();
  const qc = useQueryClient();
  const { canManageProject } = usePermissions();
  const [editing, setEditing] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [formError, setFormError] = useState<ParsedError | null>(null);

  const project = useQuery({ queryKey: ['project', id], queryFn: () => projectsApi.get(id), enabled: Number.isFinite(id) });
  const members = useQuery({ queryKey: ['members', id], queryFn: () => projectsApi.members(id), enabled: Number.isFinite(id) });

  const update = useMutation({
    mutationFn: (v: ProjectValues) =>
      projectsApi.update(id, {
        name: v.name,
        description: v.description,
        status: v.status,
        startDate: v.startDate || null,
        dueDate: v.dueDate || null,
      }),
    onSuccess: () => {
      setEditing(false);
      setFormError(null);
      qc.invalidateQueries({ queryKey: ['project', id] });
      qc.invalidateQueries({ queryKey: ['projects'] });
      qc.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (e) => setFormError(parseError(e)),
  });

  const remove = useMutation({
    mutationFn: () => projectsApi.remove(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['projects'] });
      qc.invalidateQueries({ queryKey: ['dashboard'] });
      navigate('/projects', { replace: true });
    },
  });

  if (project.isLoading) return <Spinner />;
  if (project.error || !project.data) {
    return <ErrorAlert message={project.error ? parseError(project.error).message : 'Project not found'} />;
  }
  const p = project.data;
  const manage = canManageProject(p);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <h1 className="break-words text-2xl font-bold text-slate-900">{p.name}</h1>
          <p className="text-sm text-slate-500">
            {projectStatusLabel(p.status)} · Owner: {p.owner.name} · {formatDate(p.startDate)} to {formatDate(p.dueDate)}
          </p>
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

      {p.description && <p className="whitespace-pre-wrap text-sm text-slate-700">{p.description}</p>}

      <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm" aria-labelledby="members-heading">
        <h2 id="members-heading" className="mb-2 text-sm font-semibold text-slate-700">
          Team ({p.memberCount})
        </h2>
        {members.isLoading && <Spinner />}
        <ErrorAlert message={members.error ? parseError(members.error).message : null} />
        {members.data && <MemberList projectId={id} ownerId={p.owner.id} members={members.data} canManage={manage} />}
        {manage && (
          <div className="mt-4 border-t border-slate-100 pt-4">
            <AddMemberForm projectId={id} />
          </div>
        )}
      </section>

      <ProjectTasks project={p} members={members.data ?? []} />

      {editing && (
        <Modal title="Edit project" onClose={() => setEditing(false)}>
          <ProjectForm
            project={p}
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
          title="Delete project"
          message="This permanently deletes the project, its tasks and comments."
          loading={remove.isPending}
          onConfirm={() => remove.mutate()}
          onCancel={() => setDeleting(false)}
        />
      )}
    </div>
  );
}
