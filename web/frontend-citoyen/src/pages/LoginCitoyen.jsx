import { useState } from 'react';
import { User, ShieldCheck, ArrowRight, Search, AlertCircle, Building2 } from 'lucide-react';
import { motion } from 'framer-motion';
import { loginCitoyen } from '../services/api';

export function LoginCitoyen({ onLogin, onNavigate }) {
  const [identifiant, setIdentifiant] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!identifiant.trim()) {
      setError('Veuillez entrer votre ID National ou votre numéro CIN.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const data = await loginCitoyen(identifiant);
      onLogin(data.citoyen);
    } catch (err) {
      if (err.response?.status === 404) {
        setError('Aucun citoyen trouvé avec cet identifiant.');
      } else {
        setError('Erreur de connexion au serveur. Veuillez réessayer.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pt-24 pb-20 bg-slate-50 min-h-screen flex items-center justify-center">
      <div className="max-w-md mx-auto px-4 w-full">
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="bg-white rounded-3xl shadow-xl border border-slate-100 overflow-hidden">
          <div className="absolute top-0 left-0 right-0 h-1.5 flex">
            <div className="w-1/3 bg-white h-full"></div>
            <div className="w-1/3 bg-[#D22630] h-full"></div>
            <div className="w-1/3 bg-[#007E3A] h-full"></div>
          </div>

          <div className="p-8 pt-12 text-center">
            <div className="w-16 h-16 bg-[#003A70] rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
              <User className="w-8 h-8 text-white" />
            </div>
            <h1 className="text-2xl font-black text-slate-800">Connexion Citoyen</h1>
            <p className="text-slate-500 text-sm mt-2">Accédez à votre espace personnel pour suivre vos demandes d'actes d'état civil.</p>
          </div>

          <form onSubmit={handleSubmit} className="px-8 pb-8 space-y-5">
            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-bold text-slate-600 uppercase">ID National ou Numéro CIN</label>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                <input
                  type="text"
                  value={identifiant}
                  onChange={(e) => setIdentifiant(e.target.value)}
                  placeholder="ex: 101234567890 ou ID-12345"
                  className={`w-full pl-9 pr-4 py-3 rounded-xl border text-sm font-semibold text-slate-800 outline-none transition-all ${error ? 'border-red-500 bg-red-50/30' : 'border-slate-200 focus:border-[#D22630]'}`}
                />
              </div>
              {error && (
                <div className="flex items-center gap-2 text-red-600 text-xs font-bold mt-1">
                  <AlertCircle className="w-4 h-4 flex-shrink-0" />
                  <span>{error}</span>
                </div>
              )}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-[#D22630] to-red-700 hover:from-red-700 hover:to-red-800 text-white font-bold py-3.5 px-6 rounded-xl text-sm shadow-md hover:shadow-lg transition-all duration-200 flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
                  Vérification...
                </span>
              ) : (
                <span className="flex items-center gap-2">
                  Accéder à Mon Espace <ArrowRight className="w-4 h-4" />
                </span>
              )}
            </button>

            <div className="text-center pt-2">
              <p className="text-xs text-slate-400">
                Vous n'avez pas encore de compte ?{' '}
                <button type="button" onClick={() => onNavigate('demande')} className="text-[#D22630] font-bold hover:underline">
                  Faire une demande sans compte
                </button>
              </p>
            </div>
          </form>

          <div className="px-8 pb-8">
            <div className="bg-slate-50 rounded-2xl p-4 border border-slate-100 flex items-start gap-3">
              <ShieldCheck className="w-5 h-5 text-[#007E3A] flex-shrink-0 mt-0.5" />
              <p className="text-[11px] text-slate-500 leading-relaxed">
                Vos données sont sécurisées et utilisées uniquement dans le cadre du service d'état civil de la Commune Urbaine d'Antananarivo.
              </p>
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
