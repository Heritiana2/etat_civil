import { Landmark, Mail, Phone, MapPin, Clock, ExternalLink, ShieldCheck, Heart } from 'lucide-react';

export function Footer({ onNavigate }) {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-slate-900 text-slate-300 relative overflow-hidden pt-16 pb-8">
      <div className="absolute top-0 left-0 right-0 h-1.5 flex">
        <div className="w-1/3 bg-white h-full"></div>
        <div className="w-1/3 bg-[#D22630] h-full"></div>
        <div className="w-1/3 bg-[#007E3A] h-full"></div>
      </div>
      <div className="absolute bottom-0 right-0 w-96 h-96 bg-[#007E3A]/10 rounded-full blur-3xl pointer-events-none"></div>
      <div className="absolute top-1/4 left-0 w-80 h-80 bg-[#D22630]/5 rounded-full blur-3xl pointer-events-none"></div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 mb-12">
          <div className="flex flex-col gap-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-amber-400 to-amber-600 rounded-lg p-0.5 shadow-md flex items-center justify-center">
                <div className="w-full h-full bg-slate-950 rounded-[6px] flex items-center justify-center">
                  <Landmark className="w-5 h-5 text-amber-400" />
                </div>
              </div>
              <div>
                <h3 className="text-lg font-black tracking-wider text-white">CUA ANTANANARIVO</h3>
                <p className="text-xs text-slate-400 uppercase tracking-widest font-bold">Madagascar</p>
              </div>
            </div>
            <p className="text-sm text-slate-400 leading-relaxed mt-2">
              Bâtir une capitale moderne, inclusive et connectée, au service de tous les citoyens d'Antananarivo et de Madagascar.
            </p>
            <div className="flex items-center gap-2 mt-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
              <span className="text-xs font-semibold text-emerald-400">Services municipaux en ligne 24/7</span>
            </div>
          </div>

          <div>
            <h4 className="text-white text-sm font-bold uppercase tracking-wider mb-6 border-b border-slate-800 pb-2 flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-[#D22630]" />
              E-Services Citoyens
            </h4>
            <ul className="space-y-3.5 text-sm">
              <li>
                <button onClick={() => onNavigate('demande')} className="hover:text-white transition-colors flex items-center gap-2 text-left">
                  <span className="w-1.5 h-1.5 rounded-full bg-[#007E3A]"></span>
                  Demande d'Acte de Naissance
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('demande')} className="hover:text-white transition-colors flex items-center gap-2 text-left">
                  <span className="w-1.5 h-1.5 rounded-full bg-[#007E3A]"></span>
                  Demande d'Acte de Mariage
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('demande')} className="hover:text-white transition-colors flex items-center gap-2 text-left">
                  <span className="w-1.5 h-1.5 rounded-full bg-[#007E3A]"></span>
                  Demande d'Acte de Décès
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('suivi')} className="hover:text-white transition-colors flex items-center gap-2 text-left">
                  <span className="w-1.5 h-1.5 rounded-full bg-[#D22630]"></span>
                  Suivi en Temps Réel
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('dashboard')} className="hover:text-white transition-colors flex items-center gap-2 text-left">
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-400"></span>
                  Mon Espace Personnel
                </button>
              </li>
            </ul>
          </div>

          <div>
            <h4 className="text-white text-sm font-bold uppercase tracking-wider mb-6 border-b border-slate-800 pb-2 flex items-center gap-2">
              <Clock className="w-4 h-4 text-amber-500" />
              Horaires d'Ouverture
            </h4>
            <div className="space-y-3 text-sm text-slate-400">
              <div className="flex justify-between border-b border-slate-800/50 pb-1.5">
                <span>Lundi - Jeudi</span>
                <span className="text-white font-medium">08:00 - 16:00</span>
              </div>
              <div className="flex justify-between border-b border-slate-800/50 pb-1.5">
                <span>Vendredi</span>
                <span className="text-white font-medium">08:00 - 15:00</span>
              </div>
              <div className="flex justify-between border-b border-slate-800/50 pb-1.5">
                <span>Samedi (Urgences)</span>
                <span className="text-white font-medium">09:00 - 12:00</span>
              </div>
              <div className="flex justify-between text-rose-400 font-medium pt-1">
                <span>Dimanche & Jours Fériés</span>
                <span>Fermé</span>
              </div>
              <p className="text-xs text-slate-500 italic mt-2">
                * Les demandes en ligne sont traitées dès la réouverture des bureaux.
              </p>
            </div>
          </div>

          <div>
            <h4 className="text-white text-sm font-bold uppercase tracking-wider mb-6 border-b border-slate-800 pb-2 flex items-center gap-2">
              <MapPin className="w-4 h-4 text-[#007E3A]" />
              Nous Contacter
            </h4>
            <ul className="space-y-4 text-sm text-slate-400">
              <li className="flex gap-3 items-start">
                <MapPin className="w-5 h-5 text-[#D22630] flex-shrink-0 mt-0.5" />
                <span>
                  Hôtel de Ville, Place de l'Indépendance,<br />
                  Antananarivo 101, Madagascar
                </span>
              </li>
              <li className="flex gap-3 items-center">
                <Phone className="w-5 h-5 text-[#007E3A] flex-shrink-0" />
                <span className="text-white font-semibold">+261 (0) 20 22 234 56</span>
              </li>
              <li className="flex gap-3 items-center">
                <Mail className="w-5 h-5 text-amber-500 flex-shrink-0" />
                <a href="mailto:contact@mairie-antananarivo.mg" className="hover:text-white transition-colors">
                  contact@mairie-antananarivo.mg
                </a>
              </li>
            </ul>
          </div>
        </div>

        <div className="border-t border-slate-800 pt-8 flex flex-col md:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <div className="flex items-center gap-2 flex-wrap justify-center">
            <span>© {currentYear} Commune Urbaine d'Antananarivo. Tous droits réservés.</span>
            <span className="hidden md:inline">•</span>
            <span className="flex items-center gap-1">
              Réalisé avec <Heart className="w-3.5 h-3.5 text-[#D22630] fill-[#D22630]" /> pour Madagascar
            </span>
          </div>
          <div className="flex items-center gap-6">
            <a href="#mentions" className="hover:text-slate-300 transition-colors">Mentions Légales</a>
            <a href="#confidentialite" className="hover:text-slate-300 transition-colors">Confidentialité</a>
            <a href="#aide" className="hover:text-slate-300 transition-colors flex items-center gap-1">
              Aide & FAQ <ExternalLink className="w-3 h-3" />
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
