export default function Spinner() {
  return (
    <div role="status" aria-label="Loading" className="flex justify-center py-10">
      <div className="h-8 w-8 animate-spin rounded-full border-4 border-slate-200 border-t-brand-600" />
    </div>
  );
}
