// server.js
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5000;

// Middlewares
app.use(cors());
app.use(bodyParser.json());

// Simulation d'une base de données locale pour tester avant de brancher ton db.js
let demandesActes = [
    { id_demande: 1, id_citoyen: 101, type_demande: 'Naissance', statut: 'En attente', date_demande: '2026-05-28' }
];

// 🔗 ENDPOINT 1 : Permettre au citoyen d'envoyer une demande (React)
app.post('/api/demandes/creer', (req, res) => {
    const { id_citoyen, type_demande } = req.body;
    
    if (!id_citoyen || !type_demande) {
        return res.status(400).json({ error: "Champs incomplets." });
    }

    const nouvelleDemande = {
        id_demande: demandesActes.length + 1,
        id_citoyen: parseInt(id_citoyen),
        type_demande,
        statut: 'En attente', // Par défaut
        date_demande: new Date().toISOString().split('T')[0]
    };

    demandesActes.push(nouvelleDemande);
    console.log("📥 Nouvelle demande reçue :", nouvelleDemande);
    
    res.status(201).json({ message: "Demande enregistrée avec succès !", demande: nouvelleDemande });
});

// 🔗 ENDPOINT 2 : Permettre à l'application Java (ou React) de lire les demandes
app.get('/api/demandes', (req, res) => {
    res.json(demandesActes);
});

// Lancement du serveur
app.listen(PORT, () => {
    console.log(`🚀 Serveur backend de la CUA actif sur le port ${PORT}`);
});