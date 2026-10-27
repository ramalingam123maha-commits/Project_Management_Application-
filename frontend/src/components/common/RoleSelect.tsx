import { ROLES, type Role } from '../../types';
import { roleLabel } from '../../utils/labels';

interface Props {
  name: string;
  value: Role;
  disabled?: boolean;
  onChange: (role: Role) => void;
}

export default function RoleSelect({ name, value, disabled, onChange }: Props) {
  return (
    <select
      aria-label={`Role for ${name}`}
      value={value}
      disabled={disabled}
      onChange={(e) => onChange(e.target.value as Role)}
      className="rounded-md border border-slate-300 bg-white px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:bg-slate-100"
    >
      {ROLES.map((r) => (
        <option key={r} value={r}>
          {roleLabel(r)}
        </option>
      ))}
    </select>
  );
}
