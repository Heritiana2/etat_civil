import { useState, useEffect } from 'react';
import { Menu, X, Landmark, ShieldCheck, ChevronRight, User, LogOut } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export function Navbar({ currentPage, citoyen, onNavigate, onLogout }) {
  const [isOpen, setIsOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const navItems = [
    { id: 'accueil', label: 'Accueil' },
    { id: 'demande', label: 'Faire une Demande' },
    { id: 'suivi', label: 'Suivre ma Demande' },
    { id: 'dashboard', label: 'Mon Espace' },
  ];

  const handleNavClick = (id) => {
    onNavigate(id);
    setIsOpen(false);
  };

  return (
    <nav className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
      scrolled ? 'bg-white/95 backdrop-blur-md shadow-md py-2' : 'bg-white/80 backdrop-blur-sm py-4 border-b border-slate-100'
    }`}>
      <div className="absolute top-0 left-0 right-0 h-1.5 flex">
        <div className="w-1/3 bg-white h-full shadow-sm"></div>
        <div className="w-1/3 bg-[#D22630] h-full"></div>
        <div className="w-1/3 bg-[#007E3A] h-full"></div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-3 cursor-pointer" onClick={() => handleNavClick('accueil')}>
            <div className="relative w-12 h-12 flex-shrink-0 bg-gradient-to-br from-amber-400 to-amber-600 rounded-xl p-0.5 shadow-md flex items-center justify-center overflow-hidden">
              <div className="absolute inset-0 flex">
                <div className="w-1/3 bg-white"></div>
                <div className="w-1/3 bg-[#D22630]"></div>
                <div className="w-1/3 bg-[#007E3A]"></div>
              </div>
              <div className="absolute inset-[2px] bg-slate-900 rounded-[10px] flex items-center justify-center">
                <Landmark className="w-6 h-6 text-amber-400" />
              </div>
            </div>
            <div className="flex flex-col">
              <span className="text-lg font-extrabold tracking-tight text-slate-800 leading-none flex items-center gap-1.5">
                CUA <span className="text-xs bg-[#003A70] text-white px-1.5 py-0.5 rounded font-bold uppercase tracking-wider">Antananarivo</span>
              </span>
              <span className="text-[10px] text-slate-500 font-medium tracking-wider uppercase mt-1">
                Portail Numérique Officiel
              </span>
            </div>
          </div>

          <div className="hidden lg:flex items-center gap-1">
            {navItems.map((item) => {
              const isActive = currentPage === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => handleNavClick(item.id)}
                  className={`relative px-4 py-2 rounded-lg text-sm font-semibold transition-all duration-200 ${
                    isActive ? 'text-[#D22630]' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                  }`}
                >
                  {item.label}
                  {isActive && (
                    <motion.div
                      layoutId="activeNavIndicator"
                      className="absolute bottom-0 left-4 right-4 h-0.5 bg-[#D22630] rounded-full"
                      transition={{ type: 'spring', stiffness: 380, damping: 30 }}
                    />
                  )}
                </button>
              );
            })}
          </div>

          <div className="hidden lg:flex items-center gap-3">
            {citoyen ? (
              <div className="flex items-center gap-3">
                <span className="text-sm font-bold text-slate-700">{citoyen.prenoms} {citoyen.nom}</span>
                <button onClick={onLogout} className="p-2 text-slate-400 hover:text-[#D22630] hover:bg-red-50 rounded-xl transition-all">
                  <LogOut className="w-5 h-5" />
                </button>
              </div>
            ) : (
              <>
                <button onClick={() => handleNavClick('connexion')} className="text-slate-600 hover:text-slate-900 px-4 py-2 rounded-lg text-sm font-semibold transition-all">
                  Se Connecter
                </button>
                <button onClick={() => handleNavClick('demande')} className="bg-gradient-to-r from-[#D22630] to-red-700 hover:from-red-700 hover:to-red-800 text-white px-5 py-2.5 rounded-xl text-sm font-bold shadow-md hover:shadow-lg transition-all duration-200 flex items-center gap-2">
                  <ShieldCheck className="w-4 h-4" />
                  E-Service Actes
                </button>
              </>
            )}
          </div>

          <div className="flex lg:hidden">
            <button onClick={() => setIsOpen(!isOpen)} className="inline-flex items-center justify-center p-2 rounded-lg text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition-colors">
              {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      <AnimatePresence>
        {isOpen && (
          <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }} exit={{ opacity: 0, height: 0 }} transition={{ duration: 0.2 }} className="lg:hidden bg-white border-b border-slate-100 overflow-hidden">
            <div className="px-4 pt-2 pb-6 space-y-1.5">
              {navItems.map((item) => {
                const isActive = currentPage === item.id;
                return (
                  <button key={item.id} onClick={() => handleNavClick(item.id)} className={`w-full text-left px-4 py-3 rounded-xl text-base font-bold flex items-center justify-between transition-all ${isActive ? 'bg-red-50 text-[#D22630] border-l-4 border-[#D22630]' : 'text-slate-600 hover:bg-slate-50'}`}>
                    <span>{item.label}</span>
                    <ChevronRight className={`w-4 h-4 transition-transform ${isActive ? 'text-[#D22630] translate-x-1' : 'text-slate-400'}`} />
                  </button>
                );
              })}
              {!citoyen && (
                <button onClick={() => handleNavClick('connexion')} className="w-full text-left px-4 py-3 rounded-xl text-base font-bold text-slate-600 hover:bg-slate-50 flex items-center gap-3">
                  <User className="w-5 h-5" />
                  <span>Se Connecter</span>
                </button>
              )}
              <div className="pt-4 px-4">
                <button onClick={() => handleNavClick('demande')} className="w-full bg-gradient-to-r from-[#D22630] to-red-700 text-white py-3 px-4 rounded-xl font-bold shadow-md text-center flex items-center justify-center gap-2">
                  <ShieldCheck className="w-5 h-5" />
                  Commencer une Demande
                </button>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </nav>
  );
}
