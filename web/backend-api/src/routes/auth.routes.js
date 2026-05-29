const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

router.post('/citoyen', authController.login);
router.get('/citoyen/:id', authController.verify);

module.exports = router;
