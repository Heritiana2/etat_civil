import { useState } from 'react';
import { Search, Download, ShieldCheck, CheckCircle2, FileText, Eye, AlertCircle, ArrowRight } from 'lucide-react';
import { motion } from 'framer-motion';
import { telechargerActe } from '../services/api';

const MOCK_ACTES = {
  3: { id_acte: 3, type_acte: 'Naissance', statut: 'SIGNE', date_signature: '2026-05-22', hash_blockchain: '0000a1b2c3d4e5f6...', bloc_id: 42, citoyen: { nom: 'RANDRIANASOLO', prenoms: 'Paul' } }
};

export function TelechargerActe({ onNavigate }) {
  const [searchId, setSearchId] = useState('');
  const [acte, setActe] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchId.trim()) { setError("Veuillez entrer un ID d'acte."); return; }
    setLoading(true);
    setError('');

    const mock = MOCK_ACTES[Number(searchId)];
    if (mock) {
      setTimeout(() => { setActe(mock); setLoading(false); }, 500);
    } else {
      try {
        const data = await telechargerActe(searchId);
        setActe(data);
      } catch (err) {
        setError("Acte introuvable ou pas encore signé.");
        setActe(null);
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <div className="pt-24 pb-20 bg-slate-50 min-h-screen">
      <div className="max-w-4xl mx-auto px-4 sm:px-6">
        <div className="text-center mb-10">
          <div className="inline-flex items-center gap-2 bg-emerald-600/10 border border-emerald-600/20 px-4 py-1.5 rounded-full text-emerald-600 text-xs font-bold tracking-wider uppercase mb-3">
            <Download className="w-4 h-4" /><span>Téléchargement Sécurisé</span>
          </div>
          <h1 className="text-3xl sm:text-4xl font-black text-slate-800 tracking-tight">Télécharger un Acte</h1>
          <p className="text-slate-500 text-sm sm:text-base mt-2">Récupérez votre acte signé avec QR Code et empreinte blockchain.</p>
        </div>

        <div className="bg-white rounded-3xl shadow-lg border border-slate-100 p-6 mb-8">
          <form onSubmit={handleSearch} className="flex flex-col sm:flex-row items-center gap-3">
            <div className="relative w-full flex items-center bg-slate-50 rounded-xl border border-slate-200 px-3 py-2.5">
              <Search className="w-5 h-5 text-slate-400 mr-2 flex-shrink-0" />
              <input type="text" value={searchId} onChange={(e) => setSearchId(e.target.value)} placeholder="Entrez l'ID de l'acte (ex: 3)" className="w-full bg-transparent text-slate-800 placeholder-slate-400 font-semibold outline-none text-sm" />
            </div>
            <button type="submit" disabled={loading} className="w-full sm:w-auto bg-emerald-600 hover:bg-emerald-700 text-white font-bold px-6 py-3.5 rounded-xl text-sm transition-all shadow-md flex items-center justify-center gap-2 disabled:opacity-50">
              {loading ? <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span> : <><Search className="w-4 h-4" /> Rechercher</>}
            </button>
          </form>
          {error && <div className="mt-3 flex items-center gap-2 text-red-600 text-xs font-bold"><AlertCircle className="w-4 h-4" />{error}</div>}
          {!acte && !error && <p className="text-xs text-slate-400 font-semibold mt-4">Exemple : saisissez "3" pour un acte signé (mode démo).</p>}
        </div>

        {acte && (
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="bg-white rounded-3xl shadow-xl border border-slate-100 overflow-hidden">
            <div className="bg-slate-900 text-white p-6 relative overflow-hidden">
              <div className="absolute top-0 left-0 right-0 h-1 flex">
                <div className="w-1/3 bg-white h-full"></div>
                <div className="w-1/3 bg-[#D22630] h-full"></div>
                <div className="w-1/3 bg-[#007E3A] h-full"></div>
              </div>
              <div className="flex items-center gap-4">
                <div className="p-3 bg-emerald-500/20 rounded-xl"><CheckCircle2 className="w-8 h-8 text-emerald-400" /></div>
                <div>
                  <span className="text-[10px] font-black text-emerald-400 uppercase tracking-widest">Document Officiel Signé</span>
                  <h2 className="text-xl font-black">Acte de {acte.type_acte} #{acte.id_acte}</h2>
                </div>
              </div>
            </div>

            <div className="p-6 sm:p-8 space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div className="bg-slate-50 rounded-2xl p-5 border border-slate-100">
                  <h4 className="text-xs font-black text-slate-400 uppercase tracking-wider mb-3">Détails</h4>
                  <div className="space-y-2 text-sm">
                    <div className="flex justify-between"><span className="text-slate-500">Citoyen</span><span className="font-bold text-slate-800">{acte.citoyen.nom} {acte.citoyen.prenoms}</span></div>
                    <div className="flex justify-between"><span className="text-slate-500">Type</span><span className="font-bold text-slate-800">{acte.type_acte}</span></div>
                    <div className="flex justify-between"><span className="text-slate-500">Signé le</span><span className="font-bold text-slate-800">{new Date(acte.date_signature).toLocaleDateString('fr-FR')}</span></div>
                    <div className="flex justify-between"><span className="text-slate-500">Bloc Blockchain</span><span className="font-bold text-emerald-600">#{acte.bloc_id}</span></div>
                  </div>
                </div>

                <div className="bg-slate-50 rounded-2xl p-5 border border-slate-100">
                  <h4 className="text-xs font-black text-slate-400 uppercase tracking-wider mb-3">Sécurité Blockchain</h4>
                  <div className="flex flex-col items-center justify-center h-full gap-2">
                    <ShieldCheck className="w-10 h-10 text-emerald-600" />
                    <p className="text-xs text-slate-500 text-center">Empreinte SHA-256 enregistrée dans la blockchain de la CUA</p>
                    <span className="font-mono text-[10px] text-slate-400 bg-slate-100 px-2 py-1 rounded">{acte.hash_blockchain}</span>
                  </div>
                </div>
              </div>

              <div className="flex flex-col sm:flex-row gap-4 pt-4 border-t border-slate-100">
                <button onClick={() => alert("Téléchargement du PDF sécurisé avec QR Code (simulation)")} className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-3.5 px-6 rounded-xl text-sm shadow-md transition-all flex items-center justify-center gap-2">
                  <Download className="w-4 h-4" /> Télécharger le PDF Sécurisé
                </button>
                <button onClick={() => onNavigate('suivi')} className="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold py-3.5 px-6 rounded-xl text-sm transition-all flex items-center justify-center gap-2">
                  <Eye className="w-4 h-4" /> Voir le Suivi
                </button>
              </div>

              <div className="bg-amber-50 border border-amber-100 rounded-2xl p-4 flex items-start gap-3 text-xs text-amber-800">
                <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
                <p>Ce document est une copie officielle signée numériquement. Vous pouvez vérifier son authenticité à tout moment via le hash blockchain ci-dessus.</p>
              </div>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
}
