const Citoyen = require('../models/Citoyen');

exports.login = async (req, res) => {
  try {
    const { identifiant } = req.body;
    if (!identifiant) {
      return res.status(400).json({ error: 'Veuillez fournir votre ID National ou numéro CIN.' });
    }

    const citoyen = await Citoyen.findByIdentifiant(identifiant);
    if (!citoyen) {
      return res.status(404).json({ error: 'Aucun citoyen trouvé avec cet identifiant.' });
    }

    res.json({
      message: 'Authentification réussie',
      citoyen: {
        id: citoyen.id_citoyen,
        nom: citoyen.nom,
        prenoms: citoyen.prenoms,
        sexe: citoyen.sexe,
        date_naissance: citoyen.date_naissance,
        lieu_naissance: citoyen.lieu_naissance,
        adresse: citoyen.adresse
      }
    });
  } catch (err) {
    console.error('Erreur authController.login:', err);
    res.status(500).json({ error: 'Erreur serveur lors de l\'authentification.' });
  }
};

exports.verify = async (req, res) => {
  try {
    const { id } = req.params;
    const citoyen = await Citoyen.findById(id);
    if (!citoyen) {
      return res.status(404).json({ error: 'Citoyen introuvable.' });
    }

    res.json({
      citoyen: {
        id: citoyen.id_citoyen,
        nom: citoyen.nom,
        prenoms: citoyen.prenoms,
        sexe: citoyen.sexe,
        adresse: citoyen.adresse
      }
    });
  } catch (err) {
    console.error('Erreur authController.verify:', err);
    res.status(500).json({ error: 'Erreur serveur.' });
  }
};
