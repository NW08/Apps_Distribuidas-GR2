const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/authMiddleware');
const usuarioController = require('../controllers/usuarioController');

router.get('/perfil', authMiddleware, usuarioController.getPerfil);
router.put('/perfil', authMiddleware, usuarioController.updatePerfil);

module.exports = router;
