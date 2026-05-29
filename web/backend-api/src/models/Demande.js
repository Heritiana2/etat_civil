const db = require('../../config/db');

const Demande = {
  async create(data) {
    const { type_demande, id_citoyen, details } = data;
    const [result] = await db.query(
      `INSERT INTO demande (type_demande, date_demande, statut, id_citoyen)
       VALUES (?, NOW(), 'Nouveau', ?)`,
      [type_demande, id_citoyen]
    );
    return result.insertId;
  },

  async findById(id) {
    const [rows] = await db.query(
      `SELECT d.*, c.nom, c.prenoms, c.cin
       FROM demande d
       JOIN citoyen c ON d.id_citoyen = c.id_citoyen
       WHERE d.id_demande = ?`,
      [id]
    );
    return rows[0] || null;
  },

  async findByCitoyen(idCitoyen) {
    const [rows] = await db.query(
      `SELECT * FROM demande WHERE id_citoyen = ? ORDER BY date_demande DESC`,
      [idCitoyen]
    );
    return rows;
  },

  async findAll() {
    const [rows] = await db.query(
      `SELECT d.*, c.nom, c.prenoms, c.cin
       FROM demande d
       JOIN citoyen c ON d.id_citoyen = c.id_citoyen
       ORDER BY d.date_demande DESC`
    );
    return rows;
  },

  async updateStatus(id, statut) {
    const [result] = await db.query(
      'UPDATE demande SET statut = ? WHERE id_demande = ?',
      [statut, id]
    );
    return result.affectedRows > 0;
  },

  async updateStatusAndHash(id, statut, hash) {
    const [result] = await db.query(
      'UPDATE demande SET statut = ?, hash_blockchain = ?, date_signature = NOW() WHERE id_demande = ?',
      [statut, hash, id]
    );
    return result.affectedRows > 0;
  },

  async findDemandesByType(typeActe) {
    const [rows] = await db.query(
      `SELECT d.id_demande, c.id_citoyen, c.nom, c.prenoms, d.date_demande, d.statut
       FROM demande d
       JOIN citoyen c ON d.id_citoyen = c.id_citoyen
       WHERE d.type_demande = ? AND d.statut IN ('Nouveau', 'Saisi', 'Signe')
       ORDER BY d.date_demande ASC`,
      [typeActe]
    );
    return rows;
  }
};

module.exports = Demande;
