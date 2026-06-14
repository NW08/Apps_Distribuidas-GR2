const express = require('express');
const mysql = require('mysql2/promise');
const path = require('path');

const app = express();
const PORT = 7000;

// Middleware para parsear JSON y servir archivos estáticos
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// Configuración del pool de la base de datos
const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: 'password',
    database: 'cinema',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// --- Rutas CRUD (API) ---
// CREATE: Añadir una película
app.post('/api/movies', async (req, res) => {
    const {title, director, release_year, genre} = req.body;
    try {
        const [result] = await pool.query(
            'INSERT INTO movies (title, director, release_year, genre) VALUES (?, ?, ?, ?)',
            [title, director, release_year, genre]
        );
        res.status(201).json({id: result.insertId, title, director, release_year, genre});
    } catch (error) {
        res.status(500).json({error: error.message});
    }
});

// READ: Obtener todas las películas
app.get('/api/movies', async (req, res) => {
    try {
        const [rows] = await pool.query('SELECT * FROM movies ORDER BY id DESC');
        res.json(rows);
    } catch (error) {
        res.status(500).json({error: error.message});
    }
});

// UPDATE: Actualizar una película
app.put('/api/movies/:id', async (req, res) => {
    const {id} = req.params;
    const {title, director, release_year, genre} = req.body;
    try {
        await pool.query(
            'UPDATE movies SET title = ?, director = ?, release_year = ?, genre = ? WHERE id = ?',
            [title, director, release_year, genre, id]
        );
        res.json({message: 'Movie updated successfully'});
    } catch (error) {
        res.status(500).json({error: error.message});
    }
});

// DELETE: Eliminar una película
app.delete('/api/movies/:id', async (req, res) => {
    const {id} = req.params;
    try {
        await pool.query('DELETE FROM movies WHERE id = ?', [id]);
        res.json({message: 'Movie deleted successfully'});
    } catch (error) {
        res.status(500).json({error: error.message});
    }
});

// Iniciar servidor
app.listen(PORT, () => {
    console.log(`Servidor corriendo en http://localhost:${PORT}`);
});