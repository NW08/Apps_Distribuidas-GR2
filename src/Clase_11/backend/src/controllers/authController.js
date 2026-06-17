const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const Usuario = require('../models/Usuario');

/**
 * Válida que el email tenga un formato básico válido:
 * debe contener '@' y un punto después del '@'.
 */
function esEmailValido(email) {
    const atIndex = email.indexOf('@');
    if (atIndex < 1) return false;
    const dominio = email.slice(atIndex + 1);
    return dominio.includes('.');
}

const authController = {
    /**
     * POST /api/auth/registro
     * Registra un nuevo usuario en el sistema.
     */
    async registro(req, res) {
        try {
            const {nombre, email, password} = req.body;
            
            // Validaciones
            const errores = [];

            if (!nombre || nombre.trim().length < 2) {
                errores.push('El nombre debe tener al menos 2 caracteres');
            }

            if (!email || !esEmailValido(email.trim())) {
                errores.push('El email debe tener un formato válido');
            }

            if (!password || password.length < 6) {
                errores.push('La contraseña debe tener al menos 6 caracteres');
            }

            if (errores.length > 0) {
                return res.status(400).json({errores});
            }

            // Verificar si el email ya existe
            const usuarioExistente = await Usuario.findByEmail(email.trim());
            if (usuarioExistente) {
                return res.status(409).json({error: 'El email ya está registrado'});
            }

            // Hash de la contraseña
            const passwordHash = await bcrypt.hash(password, 10);

            // Crear el usuario
            const id = await Usuario.create({
                nombre: nombre.trim(),
                email: email.trim(),
                passwordHash,
            });

            console.log("Usuario: ", nombre, email, passwordHash);

            return res.status(201).json({
                id,
                nombre: nombre.trim(),
                email: email.trim(),
            });

        } catch (err) {
            console.error('Error en registro:', err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },

    /**
     * POST /api/auth/login
     * Autentica un usuario y emite un JWT.
     */
    async login(req, res) {
        try {
            const {email, password} = req.body;

            // Buscar usuario por email
            const usuario = await Usuario.findByEmail(email);
            if (!usuario) {
                return res.status(401).json({error: 'Credenciales incorrectas'});
            }

            // Verificar contraseña
            const passwordValida = await bcrypt.compare(password, usuario.password);
            if (!passwordValida) {
                return res.status(401).json({error: 'Credenciales incorrectas'});
            }

            // Determinar si es administrador
            const isAdmin = usuario.email === 'admin@cursos.com';

            // Firmar JWT
            const token = jwt.sign(
                {id: usuario.id, email: usuario.email, isAdmin},
                process.env.JWT_SECRET,
                {expiresIn: '8h'}
            );

            return res.status(200).json({
                token,
                usuario: {
                    id: usuario.id,
                    nombre: usuario.nombre,
                    email: usuario.email,
                    isAdmin,
                },
            });
        } catch (err) {
            console.error('Error en login:', err);
            return res.status(500).json({error: 'Error interno del servidor'});
        }
    },
};

module.exports = authController;
