const Demande = require('../models/Demande');
const Citoyen = require('../models/Citoyen');

exports.creer = async (req, res) => {
  try {
    const { type_demande, id_citoyen, citoyen_info } = req.body;

    if (!type_demande || (!id_citoyen && !citoyen_info)) {
      return res.status(400).json({ error: 'Champs obligatoires manquants.' });
    }

    let citoyenId = id_citoyen;

    if (!citoyenId && citoyen_info) {
      let citoyen = await Citoyen.findByCin(citoyen_info.cin);
      if (!citoyen) {
        citoyenId = await Citoyen.create(citoyen_info);
      } else {
        citoyenId = citoyen.id_citoyen;
      }
    }

    const nouvelleDemande = {
      type_demande,
      id_citoyen: citoyenId,
      details: req.body.details || {}
    };

    const id = await Demande.create(nouvelleDemande);

    res.status(201).json({
      message: 'Demande enregistrée avec succès !',
      demande: {
        id_demande: id,
        type_demande,
        statut: 'Nouveau',
        date_demande: new Date().toISOString()
      }
    });
  } catch (err) {
    console.error('Erreur demandeController.creer:', err);
    res.status(500).json({ error: 'Erreur serveur lors de la création de la demande.' });
  }
};

exports.lister = async (req, res) => {
  try {
    const demandes = await Demande.findAll();
    res.json(demandes);
  } catch (err) {
    console.error('Erreur demandeController.lister:', err);
    res.status(500).json({ error: 'Erreur serveur.' });
  }
};

exports.listerParCitoyen = async (req, res) => {
  try {
    const { id_citoyen } = req.params;
    const demandes = await Demande.findByCitoyen(id_citoyen);
    res.json(demandes);
  } catch (err) {
    console.error('Erreur demandeController.listerParCitoyen:', err);
    res.status(500).json({ error: 'Erreur serveur.' });
  }
};
