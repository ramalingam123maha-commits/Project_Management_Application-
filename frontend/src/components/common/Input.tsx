import { forwardRef } from 'react';
import type { InputHTMLAttributes, SelectHTMLAttributes, TextareaHTMLAttributes } from 'react';

const base =
  'block w-full rounded-md border bg-white px-3 py-2 text-sm text-slate-900 shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:bg-slate-100';

function Field({ label, error, id, children }: { label: string; error?: string; id: string; children: React.ReactNode }) {
  return (
    <div>
      <label htmlFor={id} className="mb-1 block text-sm font-medium text-slate-700">
        {label}
      </label>
      {children}
      {error && (
        <p id={`${id}-error`} role="alert" className="mt-1 text-sm text-red-600">
          {error}
        </p>
      )}
    </div>
  );
}

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
}

const Input = forwardRef<HTMLInputElement, InputProps>(({ label, error, id, ...rest }, ref) => {
  const fid = id ?? rest.name ?? label;
  return (
    <Field label={label} error={error} id={fid}>
      <input
        ref={ref}
        id={fid}
        aria-invalid={!!error}
        aria-describedby={error ? `${fid}-error` : undefined}
        className={`${base} ${error ? 'border-red-500' : 'border-slate-300'}`}
        {...rest}
      />
    </Field>
  );
});
Input.displayName = 'Input';
export default Input;

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label: string;
  error?: string;
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(({ label, error, id, children, ...rest }, ref) => {
  const fid = id ?? rest.name ?? label;
  return (
    <Field label={label} error={error} id={fid}>
      <select
        ref={ref}
        id={fid}
        aria-invalid={!!error}
        className={`${base} ${error ? 'border-red-500' : 'border-slate-300'}`}
        {...rest}
      >
        {children}
      </select>
    </Field>
  );
});
Select.displayName = 'Select';

interface TextAreaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label: string;
  error?: string;
}

export const TextArea = forwardRef<HTMLTextAreaElement, TextAreaProps>(({ label, error, id, ...rest }, ref) => {
  const fid = id ?? rest.name ?? label;
  return (
    <Field label={label} error={error} id={fid}>
      <textarea
        ref={ref}
        id={fid}
        rows={3}
        aria-invalid={!!error}
        className={`${base} ${error ? 'border-red-500' : 'border-slate-300'}`}
        {...rest}
      />
    </Field>
  );
});
TextArea.displayName = 'TextArea';
