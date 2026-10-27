import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import Sidebar from './Sidebar';

export default function AppLayout() {
  const [open, setOpen] = useState(false);
  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <Navbar onMenu={() => setOpen(true)} />
      <div className="flex flex-1">
        <aside className="hidden w-56 shrink-0 border-r border-slate-200 bg-white md:block">
          <Sidebar />
        </aside>
        {open && (
          <div className="fixed inset-0 z-40 md:hidden" onClick={() => setOpen(false)}>
            <div className="absolute inset-0 bg-black/40" />
            <aside className="absolute left-0 top-0 h-full w-60 bg-white shadow-xl" onClick={(e) => e.stopPropagation()}>
              <div className="border-b border-slate-200 p-4 font-bold text-indigo-600">ProjectFlow</div>
              <Sidebar onNavigate={() => setOpen(false)} />
            </aside>
          </div>
        )}
        <main className="min-w-0 flex-1 p-4 sm:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
