import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Button from '../common/Button';
import Input, { Select, TextArea } from '../common/Input';
import ErrorAlert from '../common/ErrorAlert';
import { PROJECT_STATUSES, type Project } from '../../types';
import { projectSchema, type ProjectValues } from '../../validation/schemas';
import { projectStatusLabel } from '../../utils/labels';

interface Props {
  project?: Project;
  submitLabel: string;
  error?: string | null;
  fieldErrors?: Record<string, string>;
  onSubmit: (values: ProjectValues) => Promise<void> | void;
  onCancel: () => void;
}

export default function ProjectForm({ project, submitLabel, error, fieldErrors = {}, onSubmit, onCancel }: Props) {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ProjectValues>({
    resolver: zodResolver(projectSchema),
    defaultValues: {
      name: project?.name ?? '',
      description: project?.description ?? '',
      status: project?.status ?? 'ACTIVE',
      startDate: project?.startDate ?? '',
      dueDate: project?.dueDate ?? '',
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
      <ErrorAlert message={error} />
      <Input label="Name" error={errors.name?.message ?? fieldErrors.name} {...register('name')} />
      <TextArea label="Description" rows={3} error={errors.description?.message ?? fieldErrors.description} {...register('description')} />
      <Select label="Status" error={errors.status?.message} {...register('status')}>
        {PROJECT_STATUSES.map((s) => (
          <option key={s} value={s}>
            {projectStatusLabel(s)}
          </option>
        ))}
      </Select>
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <Input label="Start date" type="date" error={errors.startDate?.message} {...register('startDate')} />
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
