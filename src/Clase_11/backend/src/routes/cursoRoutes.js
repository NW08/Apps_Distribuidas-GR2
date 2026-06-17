const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/authMiddleware');
const cursoController = require('../controllers/cursoController');

// IMPORTANTE: /mis-cursos debe ir antes que /:id/comprar para evitar colisiones
router.get('/mis-cursos', authMiddleware, cursoController.misCursos);
router.get('/', authMiddleware, cursoController.listarCursos);
router.post('/:id/comprar', authMiddleware, cursoController.comprarCurso);

module.exports = router;
