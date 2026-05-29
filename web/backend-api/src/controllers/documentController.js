const Demande = require('../models/Demande');
const ActeBlock = require('../models/ActeBlock');

exports.telecharger = async (req, res) => {
  try {
    const { id_acte } = req.params;

    if (!id_acte) {
      return res.status(400).json({ error: 'ID de l\'acte requis.' });
    }

    const demande = await Demande.findById(id_acte);
    if (!demande) {
      return res.status(404).json({ error: 'Acte introuvable.' });
    }

    if (demande.statut !== 'SIGNE') {
      return res.status(400).json({ error: 'L\'acte n\'est pas encore signé.' });
    }

    const bloc = await ActeBlock.findByDemande(id_acte);

    res.json({
      id_acte: demande.id_demande,
      type_acte: demande.type_demande,
      statut: demande.statut,
      date_signature: demande.date_signature,
      hash_blockchain: demande.hash_blockchain,
      bloc_id: bloc ? bloc.id_bloc : null,
      citoyen: {
        nom: demande.nom,
        prenoms: demande.prenoms
      },
      message: 'Document sécurisé disponible. Le PDF sera généré avec QR Code et empreinte blockchain.'
    });
  } catch (err) {
    console.error('Erreur documentController.telecharger:', err);
    res.status(500).json({ error: 'Erreur serveur lors de la récupération du document.' });
  }
};

exports.verifier = async (req, res) => {
  try {
    const { hash } = req.params;
    if (!hash) {
      return res.status(400).json({ error: 'Hash requis.' });
    }

    const [rows] = await require('../../config/db').query(
      'SELECT * FROM bloc WHERE hash = ?',
      [hash]
    );

    if (rows.length === 0) {
      return res.status(404).json({ valide: false, message: 'Aucun bloc trouvé avec ce hash.' });
    }

    res.json({
      valide: true,
      bloc: rows[0]
    });
  } catch (err) {
    console.error('Erreur documentController.verifier:', err);
    res.status(500).json({ error: 'Erreur serveur.' });
  }
};
