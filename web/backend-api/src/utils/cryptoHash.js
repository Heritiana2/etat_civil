const crypto = require('crypto');

function generateSHA256(data) {
  return crypto.createHash('sha256').update(data).digest('hex');
}

function verifyHash(data, hash) {
  return generateSHA256(data) === hash;
}

function hashDemandeData(demande) {
  const payload = `${demande.id_demande}|${demande.type_demande}|${demande.date_demande}|${demande.id_citoyen}`;
  return generateSHA256(payload);
}

module.exports = { generateSHA256, verifyHash, hashDemandeData };
