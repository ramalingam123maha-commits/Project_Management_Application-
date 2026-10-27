import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { AxiosError } from 'axios';
import LoginPage from '../pages/LoginPage';

const login = vi.fn();
vi.mock('../context/AuthContext', () => ({ useAuth: () => ({ user: null, login }) }));

const renderPage = () =>
  render(
    <MemoryRouter>
      <LoginPage />
    </MemoryRouter>,
  );

describe('LoginPage', () => {
  beforeEach(() => {
    login.mockReset();
  });

  it('shows validation errors and does not call the API for empty input', async () => {
    renderPage();
    await userEvent.click(screen.getByRole('button', { name: /sign in/i }));
    expect(await screen.findAllByRole('alert')).not.toHaveLength(0);
    expect(login).not.toHaveBeenCalled();
  });

  it('rejects a malformed email', async () => {
    renderPage();
    await userEvent.type(screen.getByLabelText(/email/i), 'not-an-email');
    await userEvent.type(screen.getByLabelText(/password/i), 'secret123');
    await userEvent.click(screen.getByRole('button', { name: /sign in/i }));
    await waitFor(() => expect(screen.getAllByRole('alert').length).toBeGreaterThan(0));
    expect(login).not.toHaveBeenCalled();
  });

  it('submits valid credentials', async () => {
    login.mockResolvedValue(undefined);
    renderPage();
    await userEvent.type(screen.getByLabelText(/email/i), 'user@example.com');
    await userEvent.type(screen.getByLabelText(/password/i), 'secret123');
    await userEvent.click(screen.getByRole('button', { name: /sign in/i }));
    await waitFor(() => expect(login).toHaveBeenCalledWith('user@example.com', 'secret123'));
  });

  it('shows a friendly message when the server rejects the login', async () => {
    login.mockImplementation(async () => {
      throw new AxiosError('boom', 'E', undefined, null, {
        status: 401,
        data: { message: 'Authentication required' },
      } as never);
    });
    renderPage();
    await userEvent.type(screen.getByLabelText(/email/i), 'user@example.com');
    await userEvent.type(screen.getByLabelText(/password/i), 'wrongpass1');
    await userEvent.click(screen.getByRole('button', { name: /sign in/i }));
    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent(/invalid email or password/i));
  });
});
