import { useState } from 'react';
import { FileText, Search, ShieldCheck, ArrowRight, TrendingUp, Users, Calendar, Clock, MapPin, Award, ChevronRight, HelpCircle, ChevronDown, CheckCircle2, Eye, Building2 } from 'lucide-react';
import { motion } from 'framer-motion';

export function Home({ onNavigate, onSetTrackId }) {
  const [searchTrackId, setSearchTrackId] = useState('');
  const [searchError, setSearchError] = useState('');
  const [activeFaq, setActiveFaq] = useState(null);

  const handleTrackSearch = (e) => {
    e.preventDefault();
    if (!searchTrackId.trim()) {
      setSearchError('Veuillez entrer un numéro de suivi.');
      return;
    }
    setSearchError('');
    onSetTrackId(searchTrackId.trim());
    onNavigate('suivi');
  };

  const quickServices = [
    { title: "Acte de Naissance", description: "Demande de copie intégrale ou d'extrait d'acte de naissance.", icon: FileText, color: "border-l-4 border-emerald-600", iconColor: "text-emerald-600 bg-emerald-50", action: () => onNavigate('demande') },
    { title: "Acte de Mariage", description: "Demande de copie intégrale d'acte de mariage célébré à Antananarivo.", icon: FileText, color: "border-l-4 border-[#D22630]", iconColor: "text-[#D22630] bg-red-50", action: () => onNavigate('demande') },
    { title: "Acte de Décès", description: "Déclaration ou demande de copie d'acte de décès.", icon: FileText, color: "border-l-4 border-amber-500", iconColor: "text-amber-500 bg-amber-50", action: () => onNavigate('demande') },
    { title: "Suivi en Temps Réel", description: "Suivez l'évolution de votre demande d'acte en saisissant votre code.", icon: Search, color: "border-l-4 border-blue-600", iconColor: "text-blue-600 bg-blue-50", action: () => onNavigate('suivi') }
  ];

  const stats = [
    { value: "1 400 000+", label: "Citoyens rattachés", icon: Users, color: "text-emerald-600" },
    { value: "85 000+", label: "Actes traités en 2024", icon: FileText, color: "text-[#D22630]" },
    { value: "48 Heures", label: "Délai moyen d'obtention", icon: Clock, color: "text-amber-500" },
    { value: "94.2%", label: "Taux de satisfaction", icon: Award, color: "text-blue-600" }
  ];

  const faqs = [
    { q: "Combien coûte la demande d'un acte d'état civil en ligne ?", a: "Le service de demande d'acte d'état civil en ligne est entièrement gratuit. Seuls les frais de livraison postale restent à votre charge. Le retrait au guichet et le téléchargement du PDF sécurisé sont 100% gratuits." },
    { q: "Quel est le délai d'obtention d'un acte de naissance ?", a: "Avec notre nouveau portail numérique, le délai moyen de traitement est de 48 heures ouvrées. Si vous choisissez le mode 'PDF Sécurisé', l'acte signé numériquement est disponible en téléchargement immédiat dès sa validation." },
    { q: "Quelles sont les pièces justificatives à fournir ?", a: "Pour une copie intégrale d'acte de naissance, vous devez téléverser un scan ou une photo nette de votre Carte d'Identité Nationale (CIN) ou de votre livret de famille." },
    { q: "Comment fonctionne le suivi en temps réel de ma demande ?", a: "Dès la soumission de votre demande, un code unique vous est attribué. En saisissant ce code sur notre page de suivi, vous pouvez voir exactement à quelle étape se trouve votre dossier." }
  ];

  return (
    <div className="pt-20">
      <section className="relative min-h-[85vh] flex items-center justify-center bg-slate-950 overflow-hidden py-16">
        <div className="absolute inset-0 z-0">
          <div className="w-full h-full bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950"></div>
          <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-900/80 to-slate-950/40"></div>
          <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-[#D22630]/20 rounded-full blur-3xl"></div>
          <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-[#007E3A]/20 rounded-full blur-3xl"></div>
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 text-center flex flex-col items-center">
          <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6 }} className="inline-flex items-center gap-2.5 bg-white/10 backdrop-blur-md border border-white/20 px-4 py-2 rounded-full text-white text-xs sm:text-sm font-bold tracking-wider uppercase mb-8">
            <span className="flex h-2 w-2 relative">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
            </span>
            <span>Salama • Bienvenue à Antananarivo</span>
          </motion.div>

          <motion.h1 initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.1 }} className="text-4xl sm:text-5xl md:text-6xl font-black text-white leading-tight max-w-4xl tracking-tight">
            Vos démarches administratives en un <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 via-white to-red-400">clic sécurisé</span>
          </motion.h1>

          <motion.p initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.2 }} className="text-slate-300 text-base sm:text-lg md:text-xl max-w-2xl mt-6 leading-relaxed">
            Commandez vos actes de naissance, mariage ou décès en ligne et suivez l'avancement de votre dossier en temps réel, sans déplacement à la Mairie.
          </motion.p>

          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.3 }} className="w-full max-w-2xl mt-10">
            <form onSubmit={handleTrackSearch} className="bg-white p-2 rounded-2xl shadow-2xl flex flex-col sm:flex-row items-center gap-2 border-2 border-white/10 hover:border-emerald-500/50 transition-all duration-300">
              <div className="flex items-center gap-3 px-3 py-2 w-full">
                <Search className="w-5 h-5 text-slate-400 flex-shrink-0" />
                <input type="text" value={searchTrackId} onChange={(e) => setSearchTrackId(e.target.value)} placeholder="Saisissez votre code de suivi" className="w-full text-slate-800 placeholder-slate-400 bg-transparent outline-none text-sm sm:text-base font-semibold" />
              </div>
              <button type="submit" className="w-full sm:w-auto bg-gradient-to-r from-[#D22630] to-red-700 hover:from-red-700 hover:to-red-800 text-white font-bold px-6 py-3.5 rounded-xl text-sm tracking-wide shadow-md transition-all duration-200 flex items-center justify-center gap-2 whitespace-nowrap">
                Suivre ma Demande <ArrowRight className="w-4 h-4" />
              </button>
            </form>
            {searchError && <p className="text-red-400 text-xs mt-2 font-bold text-left pl-4">{searchError}</p>}
            <p className="text-slate-400 text-xs mt-3 flex items-center justify-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400" />
              Portail sécurisé crypté SSL conforme aux normes nationales malagasy.
            </p>
          </motion.div>
        </div>
      </section>

      <section className="relative z-20 -mt-16 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {quickServices.map((service, index) => {
            const IconComponent = service.icon;
            return (
              <motion.div key={index} initial={{ opacity: 0, y: 30 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ duration: 0.5, delay: index * 0.1 }} className={`bg-white rounded-2xl shadow-xl hover:shadow-2xl p-6 transition-all duration-300 cursor-pointer group hover:-translate-y-1 border border-slate-100 ${service.color}`} onClick={service.action}>
                <div className="flex justify-between items-start mb-4">
                  <div className={`p-3 rounded-xl ${service.iconColor} transition-colors duration-300`}><IconComponent className="w-6 h-6" /></div>
                  <ChevronRight className="w-5 h-5 text-slate-300 group-hover:text-slate-600 transition-colors" />
                </div>
                <h3 className="text-lg font-bold text-slate-800 mb-2 group-hover:text-slate-900">{service.title}</h3>
                <p className="text-slate-500 text-sm leading-relaxed">{service.description}</p>
              </motion.div>
            );
          })}
        </div>
      </section>

      <section className="py-20 bg-slate-50 overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            <div className="lg:col-span-5 relative flex justify-center">
              <div className="relative w-72 h-96 sm:w-80 sm:h-[420px] rounded-2xl overflow-hidden shadow-2xl z-10 border-4 border-white bg-gradient-to-br from-[#003A70] to-slate-800 flex items-center justify-center">
                <Building2 className="w-32 h-32 text-white/30" />
                <div className="absolute bottom-4 left-0 right-0 h-10 bg-slate-900/80 backdrop-blur-sm flex items-center justify-center text-white text-xs font-bold tracking-widest uppercase">
                  Hôtel de Ville • Antananarivo
                </div>
              </div>
              <div className="absolute -top-6 -left-6 w-32 h-32 bg-emerald-600/10 rounded-full blur-2xl -z-10"></div>
              <div className="absolute -bottom-6 -right-6 w-40 h-40 bg-[#D22630]/10 rounded-full blur-2xl -z-10"></div>
              <div className="absolute inset-0 border-2 border-dashed border-slate-300 rounded-2xl transform translate-x-4 translate-y-4 -z-20"></div>
            </div>
            <div className="lg:col-span-7 flex flex-col gap-6">
              <div className="flex items-center gap-2">
                <span className="h-1 w-12 bg-[#D22630] rounded-full"></span>
                <span className="text-xs font-bold text-[#D22630] uppercase tracking-widest">Édito Municipal</span>
              </div>
              <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-800 leading-tight">
                "Faire d'Antananarivo une capitale pionnière de la transition numérique à Madagascar"
              </h2>
              <div className="text-slate-600 text-sm sm:text-base leading-relaxed space-y-4">
                <p>Chers concitoyens, résidents et amis de notre belle capitale,</p>
                <p>C'est avec une immense fierté que nous vous présentons ce nouveau <strong>Portail Numérique de l'État Civil</strong>. À l'ère de la modernisation globale, notre commune franchit un cap décisif. Nous mettons fin aux longues files d'attente et aux déplacements fastidieux.</p>
                <p>Désormais, où que vous soyez à Antananarivo ou à l'étranger, vous pouvez commander vos actes administratifs de manière totalement dématérialisée, sécurisée et rapide.</p>
                <p>Cette plateforme est le reflet de notre engagement pour la transparence, la célérité du service public et le développement de notre capitale. Ensemble, connectons notre avenir.</p>
              </div>
              <div className="flex items-center gap-4 mt-4">
                <div className="h-12 w-1 bg-[#007E3A] rounded-full"></div>
                <div>
                  <h4 className="text-slate-800 font-extrabold text-lg">Le Maire d'Antananarivo</h4>
                  <p className="text-slate-500 text-xs font-medium uppercase tracking-wider">Commune Urbaine d'Antananarivo</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="py-16 bg-[#007E3A] text-white relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-r from-[#007E3A] via-[#007E3A]/95 to-[#007E3A]/90"></div>
        <div className="absolute top-0 left-0 w-full h-1 bg-[#D22630]"></div>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="text-center max-w-3xl mx-auto mb-12">
            <h2 className="text-3xl font-black tracking-tight">Notre impact en chiffres</h2>
            <p className="text-emerald-100 text-sm sm:text-base mt-2">La numérisation de nos services améliore quotidiennement la vie de nos administrés.</p>
          </div>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-8">
            {stats.map((stat, index) => {
              const IconComp = stat.icon;
              return (
                <div key={index} className="bg-white/10 backdrop-blur-sm border border-white/10 rounded-2xl p-6 text-center flex flex-col items-center gap-3">
                  <div className="p-3 bg-white/15 rounded-xl"><IconComp className="w-6 h-6 text-white" /></div>
                  <span className="text-3xl sm:text-4xl font-black tracking-tight">{stat.value}</span>
                  <span className="text-xs sm:text-sm font-semibold text-emerald-100">{stat.label}</span>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <section className="py-20 bg-slate-50 border-t border-slate-100">
        <div className="max-w-4xl mx-auto px-4 sm:px-6">
          <div className="text-center mb-12">
            <div className="inline-flex items-center justify-center p-3 bg-red-50 text-[#D22630] rounded-2xl mb-4">
              <HelpCircle className="w-8 h-8" />
            </div>
            <h2 className="text-3xl font-extrabold text-slate-800">Foire Aux Questions</h2>
            <p className="text-slate-500 text-sm sm:text-base mt-2">Retrouvez les réponses aux questions les plus fréquentes.</p>
          </div>
          <div className="space-y-4">
            {faqs.map((faq, index) => {
              const isOpen = activeFaq === index;
              return (
                <div key={index} className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden transition-all duration-300">
                  <button onClick={() => setActiveFaq(isOpen ? null : index)} className="w-full text-left px-6 py-5 flex justify-between items-center gap-4 hover:bg-slate-50 transition-colors">
                    <span className="font-bold text-slate-800 text-sm sm:text-base">{faq.q}</span>
                    <ChevronDown className={`w-5 h-5 text-slate-400 transition-transform duration-300 flex-shrink-0 ${isOpen ? 'rotate-180 text-[#D22630]' : ''}`} />
                  </button>
                  <div className={`transition-all duration-300 ease-in-out overflow-hidden ${isOpen ? 'max-h-60 border-t border-slate-100' : 'max-h-0'}`}>
                    <p className="p-6 text-slate-500 text-sm sm:text-base leading-relaxed bg-slate-50/50">{faq.a}</p>
                  </div>
                </div>
              );
            })}
          </div>
          <div className="bg-gradient-to-br from-slate-900 to-slate-800 rounded-3xl p-8 text-white text-center mt-12 shadow-xl relative overflow-hidden">
            <div className="absolute bottom-0 right-0 w-48 h-48 bg-[#007E3A]/20 rounded-full blur-2xl"></div>
            <div className="relative z-10 flex flex-col items-center gap-4">
              <h3 className="text-xl font-bold">Besoin d'une assistance personnalisée ?</h3>
              <p className="text-slate-300 text-sm max-w-xl">Nos agents sont à votre disposition pour vous guider par téléphone ou via notre service de suivi en ligne.</p>
              <div className="flex flex-col sm:flex-row gap-4 mt-2">
                <button onClick={() => onNavigate('connexion')} className="bg-white text-slate-900 hover:bg-slate-100 px-6 py-2.5 rounded-xl font-bold text-sm transition-all">Accéder à Mon Espace</button>
                <button onClick={() => onNavigate('suivi')} className="bg-white/10 hover:bg-white/20 border border-white/20 text-white px-6 py-2.5 rounded-xl font-bold text-sm transition-all">Suivre ma Demande</button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
