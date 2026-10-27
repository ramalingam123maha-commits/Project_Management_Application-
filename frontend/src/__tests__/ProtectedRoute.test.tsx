import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import ProtectedRoute from '../routes/ProtectedRoute';
import RoleGuard from '../routes/RoleGuard';
import type { User } from '../types';

const auth: { user: User | null; loading: boolean } = { user: null, loading: false };
vi.mock('../context/AuthContext', () => ({ useAuth: () => auth }));

const user = (role: User['role']): User => ({
  id: 1,
  name: 'Test',
  email: 't@example.com',
  role,
  createdAt: '2024-01-01T00:00:00',
});

function renderAt(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/login" element={<div>Login screen</div>} />
        <Route element={<ProtectedRoute />}>
          <Route path="/secret" element={<div>Secret content</div>} />
          <Route
            path="/admin"
            element={
              <RoleGuard roles={['ADMIN']}>
                <div>Admin content</div>
              </RoleGuard>
            }
          />
          <Route path="/404" element={<div>Not found</div>} />
        </Route>
      </Routes>
    </MemoryRouter>,
  );
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    auth.user = null;
    auth.loading = false;
  });

  it('redirects anonymous visitors to the login page', () => {
    renderAt('/secret');
    expect(screen.getByText('Login screen')).toBeInTheDocument();
    expect(screen.queryByText('Secret content')).not.toBeInTheDocument();
  });

  it('shows the page to a signed-in user', () => {
    auth.user = user('MEMBER');
    renderAt('/secret');
    expect(screen.getByText('Secret content')).toBeInTheDocument();
  });

  it('does not redirect while the session is still loading', () => {
    auth.loading = true;
    renderAt('/secret');
    expect(screen.queryByText('Login screen')).not.toBeInTheDocument();
    expect(screen.queryByText('Secret content')).not.toBeInTheDocument();
  });

  it('blocks a Member from an Admin-only page', () => {
    auth.user = user('MEMBER');
    renderAt('/admin');
    expect(screen.queryByText('Admin content')).not.toBeInTheDocument();
  });

  it('lets an Admin into an Admin-only page', () => {
    auth.user = user('ADMIN');
    renderAt('/admin');
    expect(screen.getByText('Admin content')).toBeInTheDocument();
  });
});
