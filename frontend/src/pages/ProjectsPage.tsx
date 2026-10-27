import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { projectsApi } from '../api/projects';
import { usePermissions } from '../hooks/usePermissions';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import Spinner from '../components/common/Spinner';
import ErrorAlert from '../components/common/ErrorAlert';
import EmptyState from '../components/common/EmptyState';
import ProjectCard from '../components/projects/ProjectCard';
import ProjectForm from '../components/projects/ProjectForm';
import { parseError, type ParsedError } from '../utils/errors';
import type { ProjectValues } from '../validation/schemas';

export default function ProjectsPage() {
  const qc = useQueryClient();
  const { canCreateProject } = usePermissions();
  const [open, setOpen] = useState(false);
  const [formError, setFormError] = useState<ParsedError | null>(null);
  const { data, isLoading, error } = useQuery({ queryKey: ['projects'], queryFn: projectsApi.list });

  const create = useMutation({
    mutationFn: (v: ProjectValues) =>
      projectsApi.create({
        name: v.name,
        description: v.description,
        status: v.status,
        startDate: v.startDate || null,
        dueDate: v.dueDate || null,
      }),
    onSuccess: () => {
      setOpen(false);
      setFormError(null);
      qc.invalidateQueries({ queryKey: ['projects'] });
      qc.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (e) => setFormError(parseError(e)),
  });

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-3">
        <h1 className="text-2xl font-bold text-slate-900">Projects</h1>
        {canCreateProject && <Button onClick={() => setOpen(true)}>New project</Button>}
      </div>

      {isLoading && <Spinner />}
      <ErrorAlert message={error ? parseError(error).message : null} />
      {data && data.length === 0 && (
        <EmptyState message={canCreateProject ? 'No projects yet. Create your first one.' : 'You are not a member of any project yet.'} />
      )}
      {data && data.length > 0 && (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          {data.map((p) => (
            <ProjectCard key={p.id} project={p} />
          ))}
        </div>
      )}

      {open && (
        <Modal title="New project" onClose={() => setOpen(false)}>
          <ProjectForm
            submitLabel="Create"
            error={formError?.message}
            fieldErrors={formError?.fieldErrors}
            onSubmit={async (v) => {
              await create.mutateAsync(v).catch(() => undefined);
            }}
            onCancel={() => setOpen(false)}
          />
        </Modal>
      )}
    </div>
  );
}
