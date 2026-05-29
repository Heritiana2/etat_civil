const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(bodyParser.json({ limit: '10mb' }));
app.use(bodyParser.urlencoded({ extended: true }));

const authRoutes = require('./src/routes/auth.routes');
const demandeRoutes = require('./src/routes/demande.routes');
const suiviRoutes = require('./src/routes/suivi.routes');
const documentRoutes = require('./src/routes/document.routes');

app.use('/api/auth', authRoutes);
app.use('/api/demandes', demandeRoutes);
app.use('/api/suivi', suiviRoutes);
app.use('/api/documents', documentRoutes);

app.get('/api/health', (req, res) => {
  res.json({ status: 'OK', message: 'API CUA État Civil opérationnelle', timestamp: new Date().toISOString() });
});

app.use((err, req, res, next) => {
  console.error('Erreur non gérée:', err);
  res.status(500).json({ error: 'Erreur interne du serveur.' });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`API CUA État Civil active sur le port ${PORT}`);
  console.log(`Endpoints:`);
  console.log(`  POST /api/auth/citoyen`);
  console.log(`  POST /api/demandes/creer`);
  console.log(`  GET  /api/demandes`);
  console.log(`  GET  /api/suivi/:id_demande`);
  console.log(`  GET  /api/documents/telecharger/:id_acte`);
});
