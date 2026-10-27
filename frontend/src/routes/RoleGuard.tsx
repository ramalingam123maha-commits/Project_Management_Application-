import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { Role } from '../types';

interface Props {
  roles: Role[];
  children: ReactNode;
}

/** UI-only convenience. The server enforces every permission. */
export default function RoleGuard({ roles, children }: Props) {
  const { user } = useAuth();
  if (!user || !roles.includes(user.role)) return <Navigate to="/" replace />;
  return <>{children}</>;
}
