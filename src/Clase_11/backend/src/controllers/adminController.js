const Curso = require('../models/Curso');
const Usuario = require('../models/Usuario');

const adminController = {
    /**
     * GET /api/admin/cursos
     * Retorna todos los cursos.
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
     * POST /api/admin/cursos
     * Crea un nuevo curso. Válida campos obligatorios.
     */
    async crearCurso(req, res) {
        try {
            const {nombre, descripcion, duracion, precio, estado} = req.body;

            if (!nombre || precio === undefined || precio === null || !estado) {
                return res.status(400).json({error: 'Los campos nombre, precio y estado son obligatorios'});
            }

            const insertId = await Curso.create({nombre, descripcion, duracion, precio, estado});
            return res.status(201).json({id: insertId});
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },

    /**
     * PUT /api/admin/cursos/:id
     * Actualiza un curso existente por ID.
     */
    async actualizarCurso(req, res) {
        try {
            const {id} = req.params;
            const {nombre, descripcion, duracion, precio, estado} = req.body;

            const affectedRows = await Curso.update(id,
                {nombre, descripcion, duracion, precio, estado});
            if (affectedRows === 0) {
                return res.status(404).json({error: 'Curso no encontrado'});
            }

            return res.status(200).json({mensaje: 'Curso actualizado exitosamente'});
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },

    /**
     * DELETE /api/admin/cursos/:id
     * Elimina un curso por ID.
     */
    async eliminarCurso(req, res) {
        try {
            const {id} = req.params;

            const affectedRows = await Curso.deleteById(id);
            if (affectedRows === 0) {
                return res.status(404).json({error: 'Curso no encontrado'});
            }

            return res.status(200).json({mensaje: 'Curso eliminado exitosamente'});
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },

    /**
     * GET /api/admin/usuarios
     * Retorna todos los usuarios sin incluir el hash de contraseña.
     */
    async listarUsuarios(req, res) {
        try {
            const usuarios = await Usuario.findAll();
            return res.status(200).json(usuarios);
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },

    /**
     * DELETE /api/admin/usuarios/:id
     * Elimina un usuario por ID. Protege la cuenta de admin@cursos.com.
     */
    async eliminarUsuario(req, res) {
        try {
            const {id} = req.params;

            // Obtener el usuario para verificar su email antes de eliminar
            const usuario = await Usuario.findById(id);
            if (!usuario) {
                return res.status(404).json({error: 'Usuario no encontrado'});
            }

            if (usuario.email === 'admin@cursos.com') {
                return res.status(403).json({error: 'No se puede eliminar al administrador'});
            }

            const affectedRows = await Usuario.deleteById(id);
            if (affectedRows === 0) {
                return res.status(404).json({error: 'Usuario no encontrado'});
            }

            return res.status(200).json({mensaje: 'Usuario eliminado exitosamente'});
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },
};

module.exports = adminController;
