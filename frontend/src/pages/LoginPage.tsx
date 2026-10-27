import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import ErrorAlert from '../components/common/ErrorAlert';
import { loginSchema, type LoginValues } from '../validation/schemas';
import { parseError } from '../utils/errors';

export default function LoginPage() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginValues>({ resolver: zodResolver(loginSchema) });

  if (user) return <Navigate to="/" replace />;

  const onSubmit = async (values: LoginValues) => {
    setError(null);
    try {
      await login(values.email, values.password);
      navigate('/', { replace: true });
    } catch (e) {
      const parsed = parseError(e);
      setError(parsed.message.toLowerCase().includes('authentication') ? 'Invalid email or password' : parsed.message);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 p-4">
      <div className="w-full max-w-sm rounded-xl bg-white p-6 shadow">
        <h1 className="mb-1 text-2xl font-bold text-indigo-600">ProjectFlow</h1>
        <p className="mb-6 text-sm text-slate-500">Sign in to your account</p>
        <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
          <ErrorAlert message={error} />
          <Input label="Email" type="email" autoComplete="email" error={errors.email?.message} {...register('email')} />
          <Input label="Password" type="password" autoComplete="current-password" error={errors.password?.message} {...register('password')} />
          <Button type="submit" className="w-full" loading={isSubmitting}>Sign in</Button>
        </form>
        <p className="mt-4 text-center text-sm text-slate-600">
          No account? <Link to="/register" className="font-medium text-indigo-600 hover:underline">Register</Link>
        </p>
      </div>
    </div>
  );
}
