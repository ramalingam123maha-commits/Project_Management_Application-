import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { tasksApi } from '../../api/tasks';
import { useAuth } from '../../context/AuthContext';
import Button from '../common/Button';
import { TextArea } from '../common/Input';
import ErrorAlert from '../common/ErrorAlert';
import Spinner from '../common/Spinner';
import { commentSchema } from '../../validation/schemas';
import { parseError } from '../../utils/errors';

type Values = { content: string };

interface Props {
  taskId: number;
  canModerate: boolean;
}

export default function CommentList({ taskId, canModerate }: Props) {
  const qc = useQueryClient();
  const { user } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const key = ['comments', taskId];

  const comments = useQuery({ queryKey: key, queryFn: () => tasksApi.comments(taskId) });
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<Values>({ resolver: zodResolver(commentSchema) });

  const add = useMutation({
    mutationFn: (v: Values) => tasksApi.addComment(taskId, v.content),
    onSuccess: () => {
      setError(null);
      reset();
      qc.invalidateQueries({ queryKey: key });
    },
    onError: (e) => setError(parseError(e).message),
  });

  const remove = useMutation({
    mutationFn: (commentId: number) => tasksApi.removeComment(taskId, commentId),
    onSuccess: () => qc.invalidateQueries({ queryKey: key }),
    onError: (e) => setError(parseError(e).message),
  });

  return (
    <div className="space-y-4">
      <ErrorAlert message={error} />
      {comments.isLoading && <Spinner />}
      {comments.data?.length === 0 && <p className="text-sm text-slate-500">No comments yet.</p>}
      <ul className="space-y-3">
        {comments.data?.map((c) => (
          <li key={c.id} className="rounded-md bg-slate-50 p-3 text-sm">
            <div className="flex items-center justify-between gap-2">
              <span className="font-medium text-slate-900">{c.author.name}</span>
              <span className="text-xs text-slate-500">{new Date(c.createdAt).toLocaleString()}</span>
            </div>
            {/* Rendered as text, never as HTML, so comment content cannot inject markup. */}
            <p className="mt-1 whitespace-pre-wrap break-words text-slate-700">{c.content}</p>
            {(c.author.id === user?.id || canModerate) && (
              <button
                type="button"
                onClick={() => remove.mutate(c.id)}
                className="mt-1 text-xs text-red-600 hover:underline"
              >
                Delete
              </button>
            )}
          </li>
        ))}
      </ul>
      <form onSubmit={handleSubmit((v) => add.mutate(v))} noValidate className="space-y-2">
        <TextArea label="Add a comment" rows={2} error={errors.content?.message} {...register('content')} />
        <div className="flex justify-end">
          <Button type="submit" loading={add.isPending}>
            Comment
          </Button>
        </div>
      </form>
    </div>
  );
}
