import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="flex min-h-[50vh] flex-col items-center justify-center gap-2 text-center">
      <h1 className="text-4xl font-bold text-slate-800">404</h1>
      <p className="text-slate-500">This page does not exist.</p>
      <Link to="/" className="font-medium text-indigo-600 hover:underline">Back to dashboard</Link>
    </div>
  );
}
