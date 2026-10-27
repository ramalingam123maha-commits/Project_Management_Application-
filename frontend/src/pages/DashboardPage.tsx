import { useQuery } from '@tanstack/react-query';
import { dashboardApi } from '../api/dashboard';
import { useAuth } from '../context/AuthContext';
import Spinner from '../components/common/Spinner';
import ErrorAlert from '../components/common/ErrorAlert';
import StatCard from '../components/dashboard/StatCard';
import StatusChart from '../components/dashboard/StatusChart';
import UpcomingDeadlines from '../components/dashboard/UpcomingDeadlines';
import { parseError } from '../utils/errors';

const scopeText = {
  ADMIN: 'Whole system',
  PROJECT_MANAGER: 'Projects you manage',
  MEMBER: 'Your projects and assigned tasks',
} as const;

export default function DashboardPage() {
  const { user } = useAuth();
  const { data, isLoading, error } = useQuery({ queryKey: ['dashboard'], queryFn: dashboardApi.get });

  if (isLoading) return <Spinner />;
  if (error || !data) return <ErrorAlert message={error ? parseError(error).message : 'No data'} />;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Welcome, {user?.name}</h1>
        <p className="text-sm text-slate-500">{scopeText[data.scope]}</p>
      </div>
      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <StatCard label="Projects" value={data.totalProjects} />
        <StatCard label="Active projects" value={data.activeProjects} />
        <StatCard label="Tasks" value={data.totalTasks} />
        <StatCard label="Overdue tasks" value={data.overdueTasks} tone={data.overdueTasks > 0 ? 'warning' : 'success'} />
      </div>
      <div className="grid gap-4 lg:grid-cols-2">
        <StatusChart data={data.tasksByStatus} />
        <UpcomingDeadlines tasks={data.upcomingDeadlines} />
      </div>
    </div>
  );
}
