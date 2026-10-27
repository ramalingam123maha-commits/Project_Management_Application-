import { describe, expect, it, vi } from 'vitest';
import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import TaskBoard from '../components/tasks/TaskBoard';
import type { Task, User } from '../types';

const person: User = { id: 1, name: 'Ann', email: 'ann@example.com', role: 'MEMBER', createdAt: '2024-01-01T00:00:00' };

const task = (id: number, title: string, status: Task['status'], extra: Partial<Task> = {}): Task => ({
  id,
  projectId: 1,
  projectName: 'Alpha',
  title,
  description: null,
  status,
  priority: 'MEDIUM',
  assignee: null,
  reporter: person,
  dueDate: null,
  createdAt: '2024-01-01T00:00:00',
  updatedAt: '2024-01-01T00:00:00',
  ...extra,
});

const tasks = [
  task(1, 'Write spec', 'TODO'),
  task(2, 'Build API', 'IN_PROGRESS', { assignee: person }),
  task(3, 'Ship it', 'DONE'),
  task(4, 'Old chore', 'TODO', { dueDate: '2000-01-01' }),
];

const renderBoard = (canChange = true, onStatusChange = vi.fn()) => {
  render(
    <MemoryRouter>
      <TaskBoard tasks={tasks} canChangeStatus={() => canChange} onStatusChange={onStatusChange} />
    </MemoryRouter>,
  );
  return onStatusChange;
};

describe('TaskBoard', () => {
  it('puts each task in the column for its status', () => {
    renderBoard();
    expect(within(screen.getByRole('region', { name: 'To do' })).getByText('Write spec')).toBeInTheDocument();
    expect(within(screen.getByRole('region', { name: 'In progress' })).getByText('Build API')).toBeInTheDocument();
    expect(within(screen.getByRole('region', { name: 'Done' })).getByText('Ship it')).toBeInTheDocument();
  });

  it('shows an empty message for a column with no tasks', () => {
    renderBoard();
    expect(within(screen.getByRole('region', { name: 'In review' })).getByText('No tasks')).toBeInTheDocument();
  });

  it('flags overdue tasks and shows the assignee', () => {
    renderBoard();
    expect(screen.getByText(/overdue/i)).toBeInTheDocument();
    expect(screen.getByText('Ann')).toBeInTheDocument();
  });

  it('reports a status change from the dropdown', async () => {
    const onChange = renderBoard();
    await userEvent.selectOptions(screen.getByLabelText('Status for Write spec'), 'DONE');
    expect(onChange).toHaveBeenCalledWith(expect.objectContaining({ id: 1 }), 'DONE');
  });

  it('hides the status dropdown when the user may not change status', () => {
    renderBoard(false);
    expect(screen.queryByLabelText('Status for Write spec')).not.toBeInTheDocument();
  });
});
