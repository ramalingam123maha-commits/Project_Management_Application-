import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi } from '../../api/projects';
import Button from '../common/Button';
import ConfirmDialog from '../common/ConfirmDialog';
import ErrorAlert from '../common/ErrorAlert';
import type { User } from '../../types';
import { roleLabel } from '../../utils/labels';
import { parseError } from '../../utils/errors';

interface Props {
  projectId: number;
  ownerId: number;
  members: User[];
  canManage: boolean;
}

export default function MemberList({ projectId, ownerId, members, canManage }: Props) {
  const qc = useQueryClient();
  const [target, setTarget] = useState<User | null>(null);
  const [error, setError] = useState<string | null>(null);

  const remove = useMutation({
    mutationFn: (userId: number) => projectsApi.removeMember(projectId, userId),
    onSuccess: () => {
      setTarget(null);
      qc.invalidateQueries({ queryKey: ['members', projectId] });
      qc.invalidateQueries({ queryKey: ['project', projectId] });
      qc.invalidateQueries({ queryKey: ['tasks', projectId] });
    },
    onError: (e) => {
      setTarget(null);
      setError(parseError(e).message);
    },
  });

  return (
    <div>
      <ErrorAlert message={error} />
      <ul className="divide-y divide-slate-100">
        {members.map((m) => (
          <li key={m.id} className="flex items-center justify-between gap-3 py-2 text-sm">
            <div className="min-w-0">
              <p className="truncate font-medium text-slate-900">
                {m.name} {m.id === ownerId && <span className="text-xs font-normal text-indigo-600">(owner)</span>}
              </p>
              <p className="truncate text-xs text-slate-500">
                {m.email} · {roleLabel(m.role)}
              </p>
            </div>
            {canManage && m.id !== ownerId && (
              <Button variant="ghost" onClick={() => setTarget(m)} aria-label={`Remove ${m.name}`}>
                Remove
              </Button>
            )}
          </li>
        ))}
      </ul>
      {target && (
        <ConfirmDialog
          title="Remove member"
          message={`Remove ${target.name} from this project? Their tasks in this project will become unassigned.`}
          confirmLabel="Remove"
          loading={remove.isPending}
          onConfirm={() => remove.mutate(target.id)}
          onCancel={() => setTarget(null)}
        />
      )}
    </div>
  );
}
