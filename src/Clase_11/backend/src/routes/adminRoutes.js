const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/authMiddleware');
const adminMiddleware = require('../middleware/adminMiddleware');
const adminController = require('../controllers/adminController');

// Todas las rutas requieren autenticación JWT + rol de administrador
router.get('/cursos', authMiddleware, adminMiddleware, adminController.listarCursos);
router.post('/cursos', authMiddleware, adminMiddleware, adminController.crearCurso);
router.put('/cursos/:id', authMiddleware, adminMiddleware, adminController.actualizarCurso);
router.delete('/cursos/:id', authMiddleware, adminMiddleware, adminController.eliminarCurso);
router.get('/usuarios', authMiddleware, adminMiddleware, adminController.listarUsuarios);
router.delete('/usuarios/:id', authMiddleware, adminMiddleware, adminController.eliminarUsuario);

module.exports = router;
