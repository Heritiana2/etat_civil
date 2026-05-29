const express = require('express');
const router = express.Router();
const documentController = require('../controllers/documentController');

router.get('/telecharger/:id_acte', documentController.telecharger);
router.get('/verifier/:hash', documentController.verifier);

module.exports = router;
