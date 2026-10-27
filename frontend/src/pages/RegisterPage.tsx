import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import ErrorAlert from '../components/common/ErrorAlert';
import { registerSchema, type RegisterValues } from '../validation/schemas';
import { parseError } from '../utils/errors';

export default function RegisterPage() {
  const { user, register: signUp } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    setError: setFieldError,
    formState: { errors, isSubmitting },
  } = useForm<RegisterValues>({ resolver: zodResolver(registerSchema) });

  if (user) return <Navigate to="/" replace />;

  const onSubmit = async (values: RegisterValues) => {
    setError(null);
    try {
      await signUp(values.name, values.email, values.password);
      navigate('/', { replace: true });
    } catch (e) {
      const parsed = parseError(e);
      for (const [field, message] of Object.entries(parsed.fieldErrors)) {
        if (field === 'name' || field === 'email' || field === 'password') setFieldError(field, { message });
      }
      setError(parsed.message);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 p-4">
      <div className="w-full max-w-sm rounded-xl bg-white p-6 shadow">
        <h1 className="mb-1 text-2xl font-bold text-indigo-600">ProjectFlow</h1>
        <p className="mb-6 text-sm text-slate-500">Create your account</p>
        <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
          <ErrorAlert message={error} />
          <Input label="Name" autoComplete="name" error={errors.name?.message} {...register('name')} />
          <Input label="Email" type="email" autoComplete="email" error={errors.email?.message} {...register('email')} />
          <Input label="Password" type="password" autoComplete="new-password" error={errors.password?.message} {...register('password')} />
          <Button type="submit" className="w-full" loading={isSubmitting}>Create account</Button>
        </form>
        <p className="mt-4 text-center text-sm text-slate-600">
          Already registered? <Link to="/login" className="font-medium text-indigo-600 hover:underline">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
