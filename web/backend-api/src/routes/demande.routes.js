const express = require('express');
const router = express.Router();
const demandeController = require('../controllers/demandeController');

router.post('/creer', demandeController.creer);
router.get('/', demandeController.lister);
router.get('/citoyen/:id_citoyen', demandeController.listerParCitoyen);

module.exports = router;
