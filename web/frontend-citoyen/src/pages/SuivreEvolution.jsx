import { useState, useEffect } from 'react';
import { Search, Clock, CheckCircle2, AlertCircle, ChevronRight, MessageSquare, Send, User, Landmark, ShieldCheck, Download, Printer, Sparkles, AlertTriangle } from 'lucide-react';
import { motion } from 'framer-motion';
import { StatusBadge } from '../components/StatusBadge.jsx';
import { getSuivi } from '../services/api';

const MOCK_HISTORY = {
  1: { id_demande: 1, type_demande: 'Naissance', statut: 'Nouveau', date_demande: '2026-05-28', nom: 'RAKOTO', prenoms: 'Jean', etape_actuelle: 1, label: 'Demande Reçue', description: 'Votre demande a été enregistrée et est en attente de vérification.' },
  2: { id_demande: 2, type_demande: 'Mariage', statut: 'Saisi', date_demande: '2026-05-25', nom: 'RABE', prenoms: 'Marie', etape_actuelle: 2, label: 'En Cours de Traitement', description: 'L\'agent saisit les détails de l\'acte.' },
  3: { id_demande: 3, type_demande: 'Naissance', statut: 'SIGNE', date_demande: '2026-05-20', nom: 'RANDRIANASOLO', prenoms: 'Paul', etape_actuelle: 4, label: 'Acte Signé', description: 'L\'acte a été signé et ancré dans la blockchain.' }
};

const STEPS = [
  { etape: 1, label: 'Demande Reçue', desc: 'Vérification des pièces' },
  { etape: 2, label: 'En Traitement', desc: 'Saisie par l\'agent' },
  { etape: 3, label: 'En Signature', desc: 'Validation officier' },
  { etape: 4, label: 'Signé', desc: 'Acte délivré' }
];

export function SuivreEvolution({ trackId, onSetTrackId }) {
  const [searchId, setSearchId] = useState(trackId || '');
  const [demande, setDemande] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [chatOpen, setChatOpen] = useState(true);
  const [chatMessages, setChatMessages] = useState([
    { sender: 'bot', text: "Salama ! Je suis votre assistante virtuelle de la CUA. Saisissez votre code de suivi ou posez-moi vos questions !", time: new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) }
  ]);
  const [chatInput, setChatInput] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchId.trim()) { setError("Veuillez entrer un code de suivi."); return; }
    setLoading(true);
    setError('');

    const mock = MOCK_HISTORY[Number(searchId)];
    if (mock) {
      setTimeout(() => { setDemande(mock); setLoading(false); setError(''); onSetTrackId(searchId); }, 500);
    } else {
      try {
        const data = await getSuivi(searchId);
        setDemande(data);
        onSetTrackId(searchId);
      } catch (err) {
        setDemande(null);
        setError("Aucune demande trouvée avec cet identifiant.");
      } finally {
        setLoading(false);
      }
    }
  };

  const addBotMessage = (text) => {
    setChatMessages(prev => [...prev, { sender: 'bot', text, time: new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) }]);
  };

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (!chatInput.trim()) return;
    setChatMessages(prev => [...prev, { sender: 'user', text: chatInput, time: new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) }]);
    setChatInput('');
    setTimeout(() => {
      const lower = chatInput.toLowerCase();
      let reply = "Pouvez-vous préciser votre question ? (délais, tarifs, documents, horaires)";
      if (lower.includes('delai') || lower.includes('temps') || lower.includes('jour')) reply = "Le délai moyen est de **48 heures ouvrées**. Le PDF est disponible immédiatement après signature.";
      else if (lower.includes('gratuit') || lower.includes('tarif') || lower.includes('payer')) reply = "Le service est **totalement gratuit**. Seul l'envoi postal a un coût.";
      else if (lower.includes('cin') || lower.includes('piece') || lower.includes('document')) reply = "Vous devez fournir votre **Carte d'Identité Nationale (CIN)** ou votre **Livret de Famille**.";
      else if (lower.includes('merci') || lower.includes('salama')) reply = "Misaotra betsaka ! N'hésitez pas si vous avez d'autres questions.";
      addBotMessage(reply);
    }, 1000);
  };

  const statusStepMap = { 'Nouveau': 1, 'Saisi': 2, 'Signe': 3, 'SIGNE': 4 };

  return (
    <div className="pt-24 pb-20 bg-slate-50 min-h-screen">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-10">
          <div className="inline-flex items-center gap-2 bg-[#D22630]/10 border border-[#D22630]/20 px-4 py-1.5 rounded-full text-[#D22630] text-xs font-bold tracking-wider uppercase mb-3">
            <Clock className="w-4 h-4" /><span>Suivi en Temps Réel</span>
          </div>
          <h1 className="text-3xl sm:text-4xl font-black text-slate-800 tracking-tight">Suivre l'Évolution de ma Demande</h1>
          <p className="text-slate-500 text-sm sm:text-base mt-2">Saisissez votre code unique pour voir l'avancement de votre dossier.</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          <div className="lg:col-span-8 space-y-6">
            <div className="bg-white rounded-3xl shadow-lg border border-slate-100 p-6">
              <form onSubmit={handleSearch} className="flex flex-col sm:flex-row items-center gap-3">
                <div className="relative w-full flex items-center bg-slate-50 rounded-xl border border-slate-200 px-3 py-2.5">
                  <Search className="w-5 h-5 text-slate-400 mr-2 flex-shrink-0" />
                  <input type="text" value={searchId} onChange={(e) => setSearchId(e.target.value)} placeholder="Entrez votre code (ex: CUA-12345)" className="w-full bg-transparent text-slate-800 placeholder-slate-400 font-semibold outline-none text-sm" />
                </div>
                <button type="submit" disabled={loading} className="w-full sm:w-auto bg-[#D22630] hover:bg-red-700 text-white font-bold px-6 py-3.5 rounded-xl text-sm transition-all shadow-md flex items-center justify-center gap-2 disabled:opacity-50">
                  {loading ? <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span> : <><Search className="w-4 h-4" /> Rechercher</>}
                </button>
              </form>
              {error && <div className="mt-3 flex items-center gap-2 text-red-600 text-xs font-bold"><AlertCircle className="w-4 h-4" />{error}</div>}
              {!trackId && !demande && !error && (
                <div className="mt-4 text-xs text-slate-400 font-semibold">Exemples de codes : 1, 2, 3 (mode démo)</div>
              )}
            </div>

            {demande && (
              <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="bg-white rounded-3xl shadow-xl border border-slate-100 overflow-hidden">
                <div className="bg-slate-900 text-white p-6 relative overflow-hidden">
                  <div className="absolute top-0 left-0 right-0 h-1 flex">
                    <div className="w-1/3 bg-white h-full"></div>
                    <div className="w-1/3 bg-[#D22630] h-full"></div>
                    <div className="w-1/3 bg-[#007E3A] h-full"></div>
                  </div>
                  <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                    <div>
                      <span className="text-[10px] font-black text-amber-400 uppercase tracking-widest block mb-1">Dossier d'État Civil</span>
                      <h2 className="text-xl sm:text-2xl font-black tracking-tight">Acte de {demande.type_demande} <span className="font-mono text-slate-400 text-sm">(#CUA-{demande.id_demande})</span></h2>
                    </div>
                    <StatusBadge statut={demande.statut} />
                  </div>
                </div>

                <div className="p-6 sm:p-8 border-b border-slate-100 bg-slate-50/30">
                  <div className="relative flex flex-col md:flex-row justify-between items-start md:items-center gap-6 md:gap-4">
                    <div className="absolute left-[14px] md:left-6 right-6 top-7 bottom-7 md:bottom-auto md:top-1/2 md:-translate-y-1/2 h-full md:h-1 bg-slate-200 -z-10 hidden md:block">
                      <div className="bg-emerald-500 h-full transition-all duration-500" style={{ width: `${((statusStepMap[demande.statut] || 1) - 1) / 3 * 100}%` }}></div>
                    </div>
                    {STEPS.map((s) => {
                      const currentStep = statusStepMap[demande.statut] || 1;
                      const isCompleted = currentStep >= s.etape;
                      const isActive = currentStep === s.etape;
                      return (
                        <div key={s.etape} className="flex md:flex-col items-center md:text-center gap-4 md:gap-2 flex-1 w-full">
                          <div className={`w-8 h-8 sm:w-10 sm:h-10 rounded-full flex items-center justify-center text-xs sm:text-sm font-bold border-2 transition-all duration-300 ${isCompleted ? 'bg-emerald-500 border-emerald-500 text-white shadow-md' : 'bg-white border-slate-200 text-slate-400'} ${isActive ? 'ring-4 ring-emerald-100 scale-110' : ''}`}>
                            {isCompleted ? <CheckCircle2 className="w-5 h-5" /> : s.etape}
                          </div>
                          <div className="text-left md:text-center">
                            <h4 className={`text-xs sm:text-sm font-extrabold ${isCompleted ? 'text-slate-800' : 'text-slate-400'}`}>{s.label}</h4>
                            <p className="text-[10px] text-slate-400 font-medium hidden sm:block">{s.desc}</p>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>

                <div className="p-6 sm:p-8">
                  <h3 className="text-sm font-black text-slate-400 uppercase tracking-wider mb-6">Détails du dossier</h3>
                  <div className="relative border-l-2 border-slate-100 pl-6 space-y-6">
                    <div className="relative">
                      <div className={`absolute -left-[31px] top-1.5 w-4 h-4 rounded-full border-2 bg-white border-[#D22630] ring-4 ring-red-50`}>
                        <div className="w-1.5 h-1.5 bg-[#D22630] rounded-full mx-auto mt-[1px] animate-ping"></div>
                      </div>
                      <div>
                        <h4 className="text-sm font-extrabold text-slate-900">{demande.label}</h4>
                        <p className="text-xs text-slate-500 mt-1">{demande.description}</p>
                        <span className="text-[10px] font-semibold text-slate-400 block mt-1">{new Date(demande.date_demande).toLocaleDateString('fr-FR')}</span>
                      </div>
                    </div>
                  </div>

                  {demande.statut === 'SIGNE' && (
                    <div className="mt-8 p-6 bg-emerald-50 rounded-2xl border border-emerald-100 flex flex-col sm:flex-row items-center justify-between gap-4">
                      <div className="flex items-center gap-3 text-emerald-800">
                        <ShieldCheck className="w-10 h-10 text-emerald-600 flex-shrink-0" />
                        <div>
                          <h4 className="font-extrabold text-sm">Document officiel signé !</h4>
                          <p className="text-xs text-emerald-700">Signé électroniquement avec ancrage blockchain.</p>
                        </div>
                      </div>
                      <button onClick={() => window.print()} className="bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5 px-5 rounded-xl text-xs shadow-md flex items-center gap-2 transition-all">
                        <Download className="w-4 h-4" /> Télécharger l'Acte
                      </button>
                    </div>
                  )}
                </div>
              </motion.div>
            )}

            {!demande && !error && !loading && (
              <div className="bg-white rounded-3xl shadow-md border border-slate-100 p-12 text-center text-slate-500 flex flex-col items-center gap-4">
                <div className="p-4 bg-slate-50 rounded-full border border-slate-100"><Clock className="w-10 h-10 text-slate-300" /></div>
                <div>
                  <h3 className="font-bold text-slate-800 text-base">Suivez votre demande</h3>
                  <p className="text-xs sm:text-sm mt-1">Saisissez votre code de suivi ci-dessus pour voir l'état d'avancement.</p>
                </div>
              </div>
            )}
          </div>

          <div className="lg:col-span-4">
            <div className="bg-white rounded-3xl shadow-lg border border-slate-100 overflow-hidden sticky top-24">
              <div className="bg-[#007E3A] text-white p-4 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="relative">
                    <div className="w-10 h-10 bg-white/10 rounded-xl flex items-center justify-center"><User className="w-6 h-6 text-white" /></div>
                    <span className="absolute bottom-0 right-0 w-3 h-3 rounded-full bg-emerald-400 border-2 border-[#007E3A]"></span>
                  </div>
                  <div>
                    <h3 className="font-bold text-sm">Assistante CUA</h3>
                    <p className="text-[10px] text-emerald-100">Service d'État Civil • En ligne</p>
                  </div>
                </div>
                <MessageSquare className="w-5 h-5 text-emerald-100" />
              </div>

              <div className="h-80 overflow-y-auto p-4 space-y-4 bg-slate-50/50">
                {chatMessages.map((msg, i) => (
                  <div key={i} className={`flex gap-2.5 ${msg.sender === 'bot' ? 'justify-start' : 'justify-end'}`}>
                    {msg.sender === 'bot' && <div className="w-7 h-7 rounded-lg bg-[#007E3A]/10 text-[#007E3A] flex items-center justify-center text-xs font-black">C</div>}
                    <div className={`max-w-[80%] rounded-2xl p-3 text-xs leading-relaxed ${msg.sender === 'bot' ? 'bg-white text-slate-700 shadow-sm border border-slate-100' : 'bg-[#D22630] text-white'}`}>
                      <p className="whitespace-pre-line">{msg.text}</p>
                      <span className={`text-[8px] block mt-1 text-right ${msg.sender === 'bot' ? 'text-slate-400' : 'text-red-200'}`}>{msg.time}</span>
                    </div>
                  </div>
                ))}
              </div>

              <div className="p-2 border-t border-slate-100 bg-white flex flex-wrap gap-1.5">
                <button onClick={() => setChatInput("Quels sont les délais ?")} className="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-full text-[10px] font-bold transition-all">⏱️ Délais ?</button>
                <button onClick={() => setChatInput("Est-ce gratuit ?")} className="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-full text-[10px] font-bold transition-all">💰 Tarifs ?</button>
                <button onClick={() => setChatInput("Quels documents ?")} className="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-full text-[10px] font-bold transition-all">📋 Documents ?</button>
              </div>

              <form onSubmit={handleSendMessage} className="p-3 border-t border-slate-100 bg-white flex gap-2">
                <input type="text" value={chatInput} onChange={(e) => setChatInput(e.target.value)} placeholder="Votre question..." className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold text-slate-800 outline-none focus:border-[#007E3A]" />
                <button type="submit" className="p-2 bg-[#007E3A] hover:bg-green-700 text-white rounded-xl transition-colors"><Send className="w-4 h-4" /></button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
