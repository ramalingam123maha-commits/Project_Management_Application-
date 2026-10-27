import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const link = ({ isActive }: { isActive: boolean }) =>
  `block rounded-md px-3 py-2 text-sm font-medium ${isActive ? 'bg-indigo-50 text-indigo-700' : 'text-slate-600 hover:bg-slate-100'}`;

export default function Sidebar({ onNavigate }: { onNavigate?: () => void }) {
  const { user } = useAuth();
  return (
    <nav aria-label="Main" className="space-y-1 p-4" onClick={onNavigate}>
      <NavLink to="/" end className={link}>Dashboard</NavLink>
      <NavLink to="/projects" className={link}>Projects</NavLink>
      <NavLink to="/my-tasks" className={link}>My Tasks</NavLink>
      {user?.role === 'ADMIN' && <NavLink to="/admin/users" className={link}>Users</NavLink>}
    </nav>
  );
}
