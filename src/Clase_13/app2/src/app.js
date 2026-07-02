const express = require('express');
const mysql = require('mysql2/promise');
const path = require('path');

const app = express();
const PORT = 7001;

// Middleware
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// Pool de conexión
const pool = mysql.createPool({
    host: 'mysql-master',
    user: 'root',
    password: 'password',
    database: 'paises',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

app.post('/api/entrenadores', async (req, res) => {
    const {
        nombre,
        entrenador,
        ranking_fifa,
        participaciones,
        campeonatos
    } = req.body;

    try {
        const [result] = await pool.query(
            `INSERT INTO entrenadores
            (nombre, entrenador, ranking_fifa, participaciones, campeonatos)
            VALUES (?, ?, ?, ?, ?)`,
            [
                nombre,
                entrenador,
                ranking_fifa,
                participaciones,
                campeonatos
            ]
        );

        res.status(201).json({
            id: result.insertId,
            nombre,
            entrenador,
            ranking_fifa,
            participaciones,
            campeonatos
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/entrenadores', async (req, res) => {
    try {
        const [rows] = await pool.query(
            'SELECT * FROM entrenadores ORDER BY id DESC'
        );

        res.json(rows);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/entrenadores/:id', async (req, res) => {
    const { id } = req.params;

    try {
        const [rows] = await pool.query(
            'SELECT * FROM entrenadores WHERE id = ?',
            [id]
        );

        if (rows.length === 0) {
            return res.status(404).json({
                message: 'Entrenador no encontrado'
            });
        }

        res.json(rows[0]);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.put('/api/entrenadores/:id', async (req, res) => {
    const { id } = req.params;

    const {
        nombre,
        entrenador,
        ranking_fifa,
        participaciones,
        campeonatos
    } = req.body;

    try {
        await pool.query(
            `UPDATE entrenadores
            SET nombre = ?,
                entrenador = ?,
                ranking_fifa = ?,
                participaciones = ?,
                campeonatos = ?
            WHERE id = ?`,
            [
                nombre,
                entrenador,
                ranking_fifa,
                participaciones,
                campeonatos,
                id
            ]
        );

        res.json({
            message: 'Entrenador actualizado correctamente'
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.delete('/api/entrenadores/:id', async (req, res) => {
    const { id } = req.params;

    try {
        await pool.query(
            'DELETE FROM entrenadores WHERE id = ?',
            [id]
        );

        res.json({
            message: 'Entrenador eliminado correctamente'
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.listen(PORT, () => {
    console.log(`Servidor corriendo en http://localhost:${PORT}`);
});