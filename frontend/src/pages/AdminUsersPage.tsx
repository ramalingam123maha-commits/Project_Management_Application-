import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { usersApi } from '../api/users';
import { useAuth } from '../context/AuthContext';
import Spinner from '../components/common/Spinner';
import ErrorAlert from '../components/common/ErrorAlert';
import EmptyState from '../components/common/EmptyState';
import RoleSelect from '../components/common/RoleSelect';
import type { Role } from '../types';
import { formatDate, roleLabel } from '../utils/labels';
import { parseError } from '../utils/errors';

export default function AdminUsersPage() {
  const qc = useQueryClient();
  const { user: me } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const users = useQuery({ queryKey: ['users'], queryFn: () => usersApi.list() });

  const changeRole = useMutation({
    mutationFn: ({ id, role }: { id: number; role: Role }) => usersApi.updateRole(id, role),
    onSuccess: (u) => {
      setError(null);
      setNotice(`${u.name} is now ${roleLabel(u.role)}`);
      qc.invalidateQueries({ queryKey: ['users'] });
    },
    onError: (e) => {
      setNotice(null);
      setError(parseError(e).message);
      qc.invalidateQueries({ queryKey: ['users'] });
    },
  });

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Users</h1>
        <p className="text-sm text-slate-500">Change who can create projects and manage teams.</p>
      </div>
      <ErrorAlert message={error ?? (users.error ? parseError(users.error).message : null)} />
      {notice && (
        <div role="status" className="rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
          {notice}
        </div>
      )}
      {users.isLoading && <Spinner />}
      {users.data?.length === 0 && <EmptyState message="No users found." />}
      {users.data && users.data.length > 0 && (
        <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-slate-50 text-xs uppercase text-slate-500">
              <tr>
                <th className="px-4 py-2">Name</th>
                <th className="px-4 py-2">Email</th>
                <th className="px-4 py-2">Joined</th>
                <th className="px-4 py-2">Role</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {users.data.map((u) => (
                <tr key={u.id}>
                  <td className="px-4 py-2 font-medium text-slate-900">
                    {u.name} {u.id === me?.id && <span className="text-xs font-normal text-slate-500">(you)</span>}
                  </td>
                  <td className="px-4 py-2 text-slate-600">{u.email}</td>
                  <td className="px-4 py-2 text-slate-600">{formatDate(u.createdAt.slice(0, 10))}</td>
                  <td className="px-4 py-2">
                    <RoleSelect
                      name={u.name}
                      value={u.role}
                      disabled={u.id === me?.id || changeRole.isPending}
                      onChange={(role) => changeRole.mutate({ id: u.id, role })}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
