import { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar.jsx';
import { Footer } from './components/Footer.jsx';
import { LoginCitoyen } from './pages/LoginCitoyen.jsx';
import { DashboardCitoyen } from './pages/DashboardCitoyen.jsx';
import { DemanderActe } from './pages/DemanderActe.jsx';
import { SuivreEvolution } from './pages/SuivreEvolution.jsx';
import { TelechargerActe } from './pages/TelechargerActe.jsx';
import { Home } from './pages/Home.jsx';
import { motion, AnimatePresence } from 'framer-motion';

function App() {
  const [currentPage, setCurrentPage] = useState('accueil');
  const [citoyen, setCitoyen] = useState(null);
  const [trackId, setTrackId] = useState('');

  useEffect(() => {
    const hash = window.location.hash.replace('#', '');
    if (hash.startsWith('suivi-')) {
      setCurrentPage('suivi');
      setTrackId(hash.replace('suivi-', ''));
    } else if (['accueil', 'connexion', 'dashboard', 'demande', 'suivi', 'telecharger'].includes(hash)) {
      setCurrentPage(hash);
    }
  }, []);

  const handleNavigate = (page) => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
    if (page === 'suivi' && trackId) {
      window.location.hash = `suivi-${trackId}`;
    } else {
      window.location.hash = page;
    }
    setCurrentPage(page);
  };

  const handleLogin = (citoyenData) => {
    setCitoyen(citoyenData);
    handleNavigate('dashboard');
  };

  const handleLogout = () => {
    setCitoyen(null);
    handleNavigate('accueil');
  };

  const handleSetTrackId = (id) => {
    setTrackId(id);
  };

  const renderPage = () => {
    const pageProps = {
      citoyen,
      onLogin: handleLogin,
      onLogout: handleLogout,
      onNavigate: handleNavigate,
      onSetTrackId: handleSetTrackId,
      trackId
    };

    switch (currentPage) {
      case 'accueil':
        return <Home key="accueil" {...pageProps} />;
      case 'connexion':
        return <LoginCitoyen key="connexion" {...pageProps} />;
      case 'dashboard':
        return <DashboardCitoyen key="dashboard" {...pageProps} />;
      case 'demande':
        return <DemanderActe key="demande" {...pageProps} />;
      case 'suivi':
        return <SuivreEvolution key="suivi" {...pageProps} />;
      case 'telecharger':
        return <TelechargerActe key="telecharger" {...pageProps} />;
      default:
        return <Home key="accueil" {...pageProps} />;
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-slate-50 font-sans antialiased text-slate-800">
      <Navbar currentPage={currentPage} citoyen={citoyen} onNavigate={handleNavigate} onLogout={handleLogout} />
      <main className="flex-grow">
        <AnimatePresence mode="wait">
          {renderPage()}
        </AnimatePresence>
      </main>
      <Footer onNavigate={handleNavigate} />
    </div>
  );
}

export default App;
