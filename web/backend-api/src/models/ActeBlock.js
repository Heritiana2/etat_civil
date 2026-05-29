const db = require('../../config/db');

const ActeBlock = {
  async getLastBlock() {
    const [rows] = await db.query('SELECT * FROM bloc ORDER BY id_bloc DESC LIMIT 1');
    return rows[0] || null;
  },

  async createBlock(data) {
    const { id_demande, hash, previous_hash, timestamp, data_payload } = data;
    const [result] = await db.query(
      `INSERT INTO bloc (id_demande, hash, previous_hash, timestamp, data_payload)
       VALUES (?, ?, ?, ?, ?)`,
      [id_demande, hash, previous_hash, timestamp || new Date(), data_payload]
    );
    return result.insertId;
  },

  async verifierIntegrite() {
    const [rows] = await db.query('SELECT * FROM bloc ORDER BY id_bloc ASC');
    for (let i = 1; i < rows.length; i++) {
      if (rows[i].previous_hash !== rows[i - 1].hash) {
        return { valide: false, bloqué: i + 1 };
      }
    }
    return { valide: true, total: rows.length };
  },

  async findByDemande(idDemande) {
    const [rows] = await db.query('SELECT * FROM bloc WHERE id_demande = ?', [idDemande]);
    return rows[0] || null;
  }
};

module.exports = ActeBlock;
