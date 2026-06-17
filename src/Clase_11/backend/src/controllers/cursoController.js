const Curso = require('../models/Curso');

const cursoController = {
    /**
     * GET /api/cursos
     * Retorna todos los cursos disponibles.
     */
    async listarCursos(req, res) {
        try {
            const cursos = await Curso.findAll();
            return res.status(200).json(cursos);
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },

    /**
     * POST /api/cursos/:id/comprar
     * Registra la adquisición de un curso (incrementa contador).
     */
    async comprarCurso(req, res) {
        try {
            const cursoId = req.params.id;

            const exito = await Curso.enrollUser(cursoId);
            if (!exito) {
                return res.status(404).json({error: 'Curso no encontrado'});
            }

            return res.status(200).json({mensaje: 'Curso adquirido exitosamente'});
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },

    /**
     * GET /api/cursos/mis-cursos
     * Como no hay tabla de relación, retorna todos los cursos (Mock/Fallback).
     */
    async misCursos(req, res) {
        try {
            const cursos = await Curso.findAll();
            return res.status(200).json(cursos);
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },
};

module.exports = cursoController;
