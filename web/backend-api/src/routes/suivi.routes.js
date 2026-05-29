const express = require('express');
const router = express.Router();
const suiviController = require('../controllers/suiviController');

router.get('/:id_demande', suiviController.suivre);

module.exports = router;
