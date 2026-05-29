import { useState, useEffect } from 'react';
import { FileText, Clock, CheckCircle2, Search, ArrowRight, Eye, ShieldAlert, PlusCircle, LayoutDashboard, Database } from 'lucide-react';
import { motion } from 'framer-motion';
import { StatusBadge } from '../components/StatusBadge.jsx';

const MOCK_DEMANDES = [
  { id_demande: 1, type_demande: 'Naissance', statut: 'Nouveau', date_demande: '2026-05-28', nom: 'RAKOTO', prenoms: 'Jean' },
  { id_demande: 2, type_demande: 'Mariage', statut: 'Saisi', date_demande: '2026-05-25', nom: 'RABE', prenoms: 'Marie' },
  { id_demande: 3, type_demande: 'Naissance', statut: 'SIGNE', date_demande: '2026-05-20', nom: 'RANDRIANASOLO', prenoms: 'Paul' },
];

export function DashboardCitoyen({ citoyen, onNavigate, onSetTrackId }) {
  const [demandes, setDemandes] = useState([]);
  const [filterType, setFilterType] = useState('tous');

  useEffect(() => {
    setDemandes(MOCK_DEMANDES);
  }, []);

  const filtered = demandes.filter(d => {
    if (filterType === 'tous') return true;
    if (filterType === 'actif') return d.statut !== 'SIGNE';
    if (filterType === 'pret') return d.statut === 'SIGNE';
    return d.type_demande.toLowerCase() === filterType;
  });

  const total = demandes.length;
  const active = demandes.filter(d => d.statut !== 'SIGNE').length;
  const completed = demandes.filter(d => d.statut === 'SIGNE').length;

  return (
    <div className="pt-24 pb-20 bg-slate-50 min-h-screen">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-10">
          <div>
            <div className="inline-flex items-center gap-2 bg-[#007E3A]/10 border border-[#007E3A]/20 px-4 py-1.5 rounded-full text-[#007E3A] text-xs font-bold tracking-wider uppercase mb-3">
              <LayoutDashboard className="w-4 h-4" />
              <span>E-Guichet Citoyen</span>
            </div>
            <h1 className="text-3xl sm:text-4xl font-black text-slate-800 tracking-tight">Mon Espace Personnel</h1>
            <p className="text-slate-500 text-sm sm:text-base mt-2">Gérez et suivez toutes vos demandes d'actes d'état civil.</p>
          </div>
          <button onClick={() => onNavigate('demande')} className="bg-[#D22630] hover:bg-red-700 text-white font-bold py-3 px-6 rounded-xl text-sm shadow-md hover:shadow-lg transition-all flex items-center gap-2">
            <PlusCircle className="w-5 h-5" /> Nouvelle Demande
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-2xl p-6 border border-slate-100 shadow-sm flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">Total demandes</span>
              <span className="text-3xl font-black text-slate-800 tracking-tight mt-1 block">{total}</span>
            </div>
            <div className="p-3 bg-slate-100 text-slate-500 rounded-xl"><Database className="w-6 h-6" /></div>
          </div>
          <div className="bg-white rounded-2xl p-6 border border-slate-100 shadow-sm flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">En cours</span>
              <span className="text-3xl font-black text-amber-500 tracking-tight mt-1 block">{active}</span>
            </div>
            <div className="p-3 bg-amber-50 text-amber-500 rounded-xl"><Clock className="w-6 h-6 animate-pulse" /></div>
          </div>
          <div className="bg-white rounded-2xl p-6 border border-slate-100 shadow-sm flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">Actes délivrés</span>
              <span className="text-3xl font-black text-emerald-600 tracking-tight mt-1 block">{completed}</span>
            </div>
            <div className="p-3 bg-emerald-50 text-emerald-500 rounded-xl"><CheckCircle2 className="w-6 h-6" /></div>
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-slate-100 p-4 mb-6 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 shadow-sm">
          <div className="flex flex-wrap gap-1.5 w-full sm:w-auto">
            {[
              { id: 'tous', label: 'Toutes' },
              { id: 'actif', label: 'En cours' },
              { id: 'pret', label: 'Signées' },
              { id: 'naissance', label: 'Naissance' },
              { id: 'mariage', label: 'Mariage' },
              { id: 'deces', label: 'Décès' }
            ].map((tab) => (
              <button key={tab.id} onClick={() => setFilterType(tab.id)} className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${filterType === tab.id ? 'bg-slate-900 text-white shadow-sm' : 'bg-slate-50 text-slate-500 hover:bg-slate-100'}`}>
                {tab.label}
              </button>
            ))}
          </div>
          <span className="text-xs font-semibold text-slate-400">{filtered.length} sur {total} demande(s)</span>
        </div>

        {filtered.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filtered.map((demande) => (
              <div key={demande.id_demande} className="bg-white rounded-2xl border border-slate-100 hover:border-slate-200 shadow-md hover:shadow-xl transition-all duration-300 flex flex-col overflow-hidden group">
                <div className={`h-1.5 w-full ${demande.type_demande === 'Naissance' ? 'bg-emerald-500' : demande.type_demande === 'Mariage' ? 'bg-[#D22630]' : 'bg-amber-500'}`}></div>
                <div className="p-6 flex flex-col flex-grow gap-4">
                  <div className="flex justify-between items-center">
                    <div className="flex items-center gap-1.5">
                      <span className={`w-2.5 h-2.5 rounded-full ${demande.type_demande === 'Naissance' ? 'bg-emerald-500' : demande.type_demande === 'Mariage' ? 'bg-[#D22630]' : 'bg-amber-500'}`}></span>
                      <span className="text-xs font-black text-slate-800 uppercase tracking-wider">Acte de {demande.type_demande}</span>
                    </div>
                  </div>
                  <div className="space-y-1">
                    <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider block">Demandeur</span>
                    <span className="text-sm font-bold text-slate-800 block truncate">{demande.nom} {demande.prenoms}</span>
                  </div>
                  <div className="border-t border-b border-slate-100 py-3 text-xs">
                    <span className="text-slate-400 block font-medium">Soumis le :</span>
                    <span className="font-bold text-slate-700">{new Date(demande.date_demande).toLocaleDateString('fr-FR')}</span>
                  </div>
                  <div className="space-y-1.5">
                    <div className="flex justify-between items-center">
                      <StatusBadge statut={demande.statut} />
                    </div>
                    <div className="w-full bg-slate-100 h-1.5 rounded-full overflow-hidden">
                      <div className={`h-full transition-all duration-500 ${demande.statut === 'SIGNE' ? 'bg-emerald-500 w-full' : demande.statut === 'Nouveau' ? 'bg-blue-500 w-1/4' : 'bg-amber-500 w-1/2'}`}></div>
                    </div>
                  </div>
                  <div className="pt-4 mt-auto flex items-center justify-between gap-2 border-t border-slate-100">
                    <button onClick={() => { onSetTrackId(String(demande.id_demande)); onNavigate('suivi'); }} className="w-full bg-slate-900 hover:bg-black text-white font-bold text-xs py-2.5 px-4 rounded-xl shadow transition-all flex items-center justify-center gap-1.5">
                      <Eye className="w-3.5 h-3.5" /> Suivre & Gérer
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-white rounded-3xl shadow-md border border-slate-100 p-16 text-center max-w-xl mx-auto flex flex-col items-center gap-5">
            <div className="p-4 bg-slate-50 rounded-full border border-slate-100">
              <ShieldAlert className="w-12 h-12 text-slate-300" />
            </div>
            <div>
              <h3 className="font-black text-slate-800 text-lg">Aucune demande trouvée</h3>
              <p className="text-slate-500 text-sm mt-1">Vous n'avez pas encore soumis de demandes ou le filtre est trop restrictif.</p>
            </div>
            <button onClick={() => onNavigate('demande')} className="bg-[#D22630] hover:bg-red-700 text-white font-bold py-3 px-6 rounded-xl text-xs sm:text-sm shadow-md transition-all flex items-center justify-center gap-2">
              <PlusCircle className="w-4 h-4" /> Faire ma première demande
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
