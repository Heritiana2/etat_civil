import { LayoutDashboard, FileText, Search, Download, LogOut, User } from 'lucide-react';

export function Sidebar({ currentPage, citoyen, onNavigate, onLogout }) {
  const items = [
    { id: 'dashboard', label: 'Tableau de Bord', icon: LayoutDashboard },
    { id: 'demande', label: 'Nouvelle Demande', icon: FileText },
    { id: 'suivi', label: 'Suivre une Demande', icon: Search },
    { id: 'telecharger', label: 'Télécharger un Acte', icon: Download },
  ];

  return (
    <aside className="w-64 bg-white border-r border-slate-100 min-h-screen p-4 flex flex-col">
      {citoyen && (
        <div className="flex items-center gap-3 p-4 mb-6 bg-slate-50 rounded-2xl border border-slate-100">
          <div className="w-10 h-10 bg-[#003A70] rounded-xl flex items-center justify-center text-white font-bold">
            {citoyen.prenoms?.[0]}{citoyen.nom?.[0]}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-bold text-slate-800 truncate">{citoyen.prenoms} {citoyen.nom}</p>
            <p className="text-[10px] text-slate-400 font-medium">Citoyen</p>
          </div>
        </div>
      )}

      <nav className="flex-1 space-y-1">
        {items.map((item) => {
          const Icon = item.icon;
          const isActive = currentPage === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onNavigate(item.id)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${
                isActive ? 'bg-[#D22630]/10 text-[#D22630] border-l-4 border-[#D22630]' : 'text-slate-500 hover:bg-slate-50 hover:text-slate-700'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span>{item.label}</span>
            </button>
          );
        })}
      </nav>

      <button onClick={onLogout} className="flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold text-slate-400 hover:text-[#D22630] hover:bg-red-50 transition-all mt-4">
        <LogOut className="w-5 h-5" />
        <span>Déconnexion</span>
      </button>
    </aside>
  );
}
