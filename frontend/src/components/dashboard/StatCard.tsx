interface Props {
  label: string;
  value: number | string;
  tone?: 'default' | 'warning' | 'success';
}

const tones = {
  default: 'text-slate-900',
  warning: 'text-red-600',
  success: 'text-green-600',
};

export default function StatCard({ label, value, tone = 'default' }: Props) {
  return (
    <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <p className="text-sm text-slate-500">{label}</p>
      <p className={`mt-1 text-3xl font-semibold ${tones[tone]}`}>{value}</p>
    </div>
  );
}
