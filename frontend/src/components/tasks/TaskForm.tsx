import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Button from '../common/Button';
import Input, { Select, TextArea } from '../common/Input';
import ErrorAlert from '../common/ErrorAlert';
import { TASK_PRIORITIES, TASK_STATUSES, type Task, type User } from '../../types';
import { taskSchema, type TaskValues } from '../../validation/schemas';
import { priorityLabel, statusLabel } from '../../utils/labels';

interface Props {
  task?: Task;
  members: User[];
  submitLabel: string;
  error?: string | null;
  fieldErrors?: Record<string, string>;
  onSubmit: (values: TaskValues) => Promise<void> | void;
  onCancel: () => void;
}

export default function TaskForm({ task, members, submitLabel, error, fieldErrors = {}, onSubmit, onCancel }: Props) {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<TaskValues>({
    resolver: zodResolver(taskSchema),
    defaultValues: {
      title: task?.title ?? '',
      description: task?.description ?? '',
      status: task?.status ?? 'TODO',
      priority: task?.priority ?? 'MEDIUM',
      assigneeId: task?.assignee ? String(task.assignee.id) : '',
      dueDate: task?.dueDate ?? '',
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
      <ErrorAlert message={error} />
      <Input label="Title" error={errors.title?.message ?? fieldErrors.title} {...register('title')} />
      <TextArea label="Description" rows={3} error={errors.description?.message ?? fieldErrors.description} {...register('description')} />
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <Select label="Status" {...register('status')}>
          {TASK_STATUSES.map((s) => (
            <option key={s} value={s}>
              {statusLabel(s)}
            </option>
          ))}
        </Select>
        <Select label="Priority" {...register('priority')}>
          {TASK_PRIORITIES.map((p) => (
            <option key={p} value={p}>
              {priorityLabel(p)}
            </option>
          ))}
        </Select>
        <Select label="Assignee" error={fieldErrors.assigneeId} {...register('assigneeId')}>
          <option value="">Unassigned</option>
          {members.map((m) => (
            <option key={m.id} value={m.id}>
              {m.name}
            </option>
          ))}
        </Select>
        <Input label="Due date" type="date" error={errors.dueDate?.message ?? fieldErrors.dueDate} {...register('dueDate')} />
      </div>
      <div className="flex justify-end gap-2">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" loading={isSubmitting}>
          {submitLabel}
        </Button>
      </div>
    </form>
  );
}
