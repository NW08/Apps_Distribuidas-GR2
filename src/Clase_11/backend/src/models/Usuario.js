const {pool} = require('../config/db');

const Usuario = {
    /**
     * Finds a user by email, including the password field.
     * Strictly for internal use during login verification.
     */
    async findByEmail(email) {
        const [rows] = await pool.query(
            'SELECT id, nombre, email, password, fecha_registro FROM usuarios WHERE email = ?',
            [email]
        );
        return rows[0] || null;
    },

    /**
     * Finds a user by ID. Excludes the password field.
     */
    async findById(id) {
        const [rows] = await pool.query(
            'SELECT id, nombre, email, fecha_registro FROM usuarios WHERE id = ?',
            [id]
        );
        return rows[0] || null;
    },

    /**
     * Creates a new user.
     * Returns the insertId of the new record.
     */
    async create({nombre, email, passwordHash}) {
        const [result] = await pool.query(
            'INSERT INTO usuarios (nombre, email, password) VALUES (?, ?, ?)',
            [nombre, email, passwordHash]
        );
        return result.insertId;
    },

    /**
     * Updates a user's name by ID.
     * Returns the number of affected rows.
     */
    async updateName(id, name) {
        const [result] = await pool.query(
            'UPDATE usuarios SET nombre = ? WHERE id = ?',
            [name, id]
        );
        return result.affectedRows;
    },

    /**
     * Updates a user's password (hash) by ID.
     * Returns the number of affected rows.
     */
    async updatePassword(id, hash) {
        const [result] = await pool.query(
            'UPDATE usuarios SET password = ? WHERE id = ?',
            [hash, id]
        );
        return result.affectedRows;
    },

    /**
     * Returns all users without including the password field.
     */
    async findAll() {
        const [rows] = await pool.query(
            'SELECT id, nombre, email, fecha_registro FROM usuarios'
        );
        return rows;
    },

    /**
     * Deletes a user by ID.
     * Returns the number of affected rows.
     */
    async deleteById(id) {
        const [result] = await pool.query(
            'DELETE FROM usuarios WHERE id = ?',
            [id]
        );
        return result.affectedRows;
    }
};

module.exports = Usuario;