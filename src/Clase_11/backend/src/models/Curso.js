const {pool} = require('../config/db');

const Curso = {
    async findAll() {
        const [rows] = await pool.query('SELECT * FROM cursos');
        return rows;
    },

    async create({name, description, duration, price, status}) {
        const [result] = await pool.query(
            'INSERT INTO cursos (nombre, descripcion, duracion, precio, estado) VALUES (?, ?, ?, ?, ?)',
            [name, description, duration, price, status]
        );
        return result.insertId;
    },

    async update(id, {name, description, duration, price, status}) {
        const [result] = await pool.query(
            'UPDATE cursos SET nombre = ?, descripcion = ?, duracion = ?, precio = ?, estado = ? WHERE id = ?',
            [name, description, duration, price, status, id]
        );
        return result.affectedRows;
    },

    async deleteById(id) {
        const [result] = await pool.query('DELETE FROM cursos WHERE id = ?', [id]);
        return result.affectedRows;
    },

    /**
     * Incrementa el contador de accesos.
     * Al ser un único UPDATE, MySQL garantiza la consistencia (Atomicidad) automáticamente.
     */
    async enrollUser(courseId) {
        const [result] = await pool.query(
            'UPDATE cursos SET usuarios_accedidos = usuarios_accedidos + 1 WHERE id = ?',
            [courseId]
        );
        return result.affectedRows > 0; // Devuelve true si se actualizó con éxito
    }
};

module.exports = Curso;