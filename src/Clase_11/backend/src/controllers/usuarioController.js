const bcrypt = require('bcrypt');
const Usuario = require('../models/Usuario');

const usuarioController = {
    /**
     * GET /api/usuario/perfil
     * Retorna los datos del perfil del usuario autenticado.
     */
    async getPerfil(req, res) {
        try {
            const usuario = await Usuario.findById(req.user.id);
            if (!usuario) {
                return res.status(404).json({error: 'Usuario no encontrado'});
            }
            return res.status(200).json({
                id: usuario.id,
                nombre: usuario.nombre,
                email: usuario.email,
                fecha_registro: usuario.fecha_registro,
            });
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },

    /**
     * PUT /api/usuario/perfil
     * Actualiza nombre y/o contraseña del usuario autenticado.
     */
    async updatePerfil(req, res) {
        try {
            const {nombre, passwordActual, passwordNueva} = req.body;

            // Actualizar nombre si se proporciona
            if (nombre !== undefined) {
                if (typeof nombre !== 'string' || nombre.trim().length < 2) {
                    return res.status(400).json({error: 'El nombre debe tener al menos 2 caracteres'});
                }
                await Usuario.updateName(req.user.id, nombre.trim());
            }

            // Actualizar contraseña si se proporciona
            if (passwordNueva !== undefined) {
                if (!passwordActual) {
                    return res.status(400).json({error: 'Se requiere la contraseña actual para cambiar la contraseña'});
                }

                // Obtener el usuario con el hash para verificar
                const usuarioConHash = await Usuario.findByEmail(req.user.email);
                if (!usuarioConHash) {
                    return res.status(404).json({error: 'Usuario no encontrado'});
                }

                const passwordValida = await bcrypt.compare(passwordActual, usuarioConHash.password);
                if (!passwordValida) {
                    return res.status(401).json({error: 'Contraseña actual incorrecta'});
                }

                const nuevoHash = await bcrypt.hash(passwordNueva, 10);
                await Usuario.updatePassword(req.user.id, nuevoHash);
            }

            return res.status(200).json({mensaje: 'Perfil actualizado'});
        } catch (err) {
            console.error(err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },
};

module.exports = usuarioController;
