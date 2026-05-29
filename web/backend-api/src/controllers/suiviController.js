const Demande = require('../models/Demande');

exports.suivre = async (req, res) => {
  try {
    const { id_demande } = req.params;

    if (!id_demande) {
      return res.status(400).json({ error: 'ID de demande requis.' });
    }

    const demande = await Demande.findById(id_demande);
    if (!demande) {
      return res.status(404).json({ error: 'Demande introuvable.' });
    }

    const historiqueEtapes = {
      'Nouveau': { etape: 1, label: 'Demande Reçue', description: 'Votre demande a été enregistrée et est en attente de vérification.' },
      'Saisi': { etape: 2, label: 'En Cours de Traitement', description: 'L\'agent d\'état civil saisit les détails de l\'acte.' },
      'Signe': { etape: 3, label: 'En Attente de Signature', description: 'L\'acte est en attente de signature par l\'officier.' },
      'SIGNE': { etape: 4, label: 'Acte Signé', description: 'L\'acte a été signé numériquement et ancré dans la blockchain.' }
    };

    const etape = historiqueEtapes[demande.statut] || { etape: 0, label: demande.statut, description: '' };

    res.json({
      id_demande: demande.id_demande,
      type_demande: demande.type_demande,
      statut: demande.statut,
      date_demande: demande.date_demande,
      date_signature: demande.date_signature,
      hash_blockchain: demande.hash_blockchain,
      etape_actuelle: etape.etape,
      label: etape.label,
      description: etape.description,
      citoyen: {
        nom: demande.nom,
        prenoms: demande.prenoms
      }
    });
  } catch (err) {
    console.error('Erreur suiviController.suivre:', err);
    res.status(500).json({ error: 'Erreur serveur lors du suivi.' });
  }
};
