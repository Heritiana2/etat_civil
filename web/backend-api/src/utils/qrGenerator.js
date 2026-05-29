const crypto = require('crypto');

function generateQRData(acte) {
  const data = {
    id_acte: acte.id_acte,
    type: acte.type_acte,
    hash: acte.hash_blockchain,
    date_signature: acte.date_signature,
    verifier: `${process.env.API_URL || 'http://localhost:5000'}/api/documents/verifier/${acte.hash_blockchain}`
  };
  return JSON.stringify(data);
}

function generateQRCodeSVG(data) {
  const qrData = typeof data === 'string' ? data : generateQRData(data);
  const hash = crypto.createHash('sha256').update(qrData).digest('hex').substring(0, 8);
  return {
    content: qrData,
    checksum: hash,
    verification_url: `${process.env.API_URL || 'http://localhost:5000'}/api/documents/verifier/${hash}`
  };
}

module.exports = { generateQRData, generateQRCodeSVG };
