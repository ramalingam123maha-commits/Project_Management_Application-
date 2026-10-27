import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi } from '../../api/projects';
import Button from '../common/Button';
import Input from '../common/Input';
import ErrorAlert from '../common/ErrorAlert';
import { addMemberSchema } from '../../validation/schemas';
import { parseError } from '../../utils/errors';

type Values = { email: string };

export default function AddMemberForm({ projectId }: { projectId: number }) {
  const qc = useQueryClient();
  const [error, setError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<Values>({ resolver: zodResolver(addMemberSchema) });

  const add = useMutation({
    mutationFn: (v: Values) => projectsApi.addMember(projectId, v.email),
    onSuccess: () => {
      setError(null);
      reset();
      qc.invalidateQueries({ queryKey: ['members', projectId] });
      qc.invalidateQueries({ queryKey: ['project', projectId] });
    },
    onError: (e) => setError(parseError(e).message),
  });

  return (
    <form onSubmit={handleSubmit((v) => add.mutate(v))} noValidate className="space-y-2">
      <ErrorAlert message={error} />
      <div className="flex flex-col gap-2 sm:flex-row sm:items-end">
        <div className="flex-1">
          <Input label="Add member by email" type="email" error={errors.email?.message} {...register('email')} />
        </div>
        <Button type="submit" loading={add.isPending}>
          Add
        </Button>
      </div>
    </form>
  );
}
