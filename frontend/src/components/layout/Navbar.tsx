import { useAuth } from '../../context/AuthContext';
import Button from '../common/Button';

const roleLabel = { ADMIN: 'Admin', PROJECT_MANAGER: 'Project Manager', MEMBER: 'Member' } as const;

export default function Navbar({ onMenu }: { onMenu: () => void }) {
  const { user, logout } = useAuth();
  return (
    <header className="flex h-14 items-center justify-between border-b border-slate-200 bg-white px-4">
      <div className="flex items-center gap-3">
        <button onClick={onMenu} aria-label="Open menu" className="rounded p-2 text-slate-600 hover:bg-slate-100 md:hidden">
          ☰
        </button>
        <span className="text-lg font-bold text-indigo-600">ProjectFlow</span>
      </div>
      <div className="flex items-center gap-3">
        <div className="hidden text-right text-sm sm:block">
          <div className="font-medium text-slate-900">{user?.name}</div>
          <div className="text-xs text-slate-500">{user ? roleLabel[user.role] : ''}</div>
        </div>
        <Button variant="secondary" onClick={logout}>Log out</Button>
      </div>
    </header>
  );
}
