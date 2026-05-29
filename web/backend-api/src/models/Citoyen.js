const db = require('../../config/db');

const Citoyen = {
  async findById(id) {
    const [rows] = await db.query('SELECT * FROM citoyen WHERE id_citoyen = ?', [id]);
    return rows[0] || null;
  },

  async findByCin(cin) {
    const [rows] = await db.query('SELECT * FROM citoyen WHERE cin = ?', [cin]);
    return rows[0] || null;
  },

  async findByIdentifiant(identifiant) {
    const [rows] = await db.query(
      'SELECT * FROM citoyen WHERE id_citoyen = ? OR cin = ?',
      [identifiant, identifiant]
    );
    return rows[0] || null;
  },

  async create(data) {
    const { nom, prenoms, sexe, date_naissance, lieu_naissance, adresse, statut_matrimonial, nom_pere, nom_mere } = data;
    const [result] = await db.query(
      `INSERT INTO citoyen (nom, prenoms, sexe, date_naissance, lieu_naissance, adresse, statut_matrimonial, nom_pere, nom_mere)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [nom, prenoms, sexe, date_naissance, lieu_naissance, adresse, statut_matrimonial || 'Celibataire', nom_pere, nom_mere]
    );
    return result.insertId;
  }
};

module.exports = Citoyen;
