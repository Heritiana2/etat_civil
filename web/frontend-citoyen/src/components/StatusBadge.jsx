export function StatusBadge({ statut }) {
  const styles = {
    'Nouveau': 'bg-blue-100 text-blue-700 border-blue-200',
    'Saisi': 'bg-amber-100 text-amber-700 border-amber-200',
    'Signe': 'bg-purple-100 text-purple-700 border-purple-200',
    'SIGNE': 'bg-emerald-100 text-emerald-700 border-emerald-200',
  };

  const labels = {
    'Nouveau': 'Nouveau',
    'Saisi': 'En Saisie',
    'Signe': 'En Signature',
    'SIGNE': 'Signé',
  };

  const color = styles[statut] || 'bg-slate-100 text-slate-600 border-slate-200';
  const label = labels[statut] || statut;

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider border ${color}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${statut === 'SIGNE' ? 'bg-emerald-500' : statut === 'Nouveau' ? 'bg-blue-500' : 'bg-amber-500'} ${statut !== 'SIGNE' ? 'animate-pulse' : ''}`}></span>
      {label}
    </span>
  );
}
