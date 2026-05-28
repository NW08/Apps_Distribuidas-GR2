const express = require('express');
const mysql = require('mysql2/promise');
const path = require('path');

const app = express();
const port = 7000;

app.use(express.json());

app.use(express.static(path.join(__dirname, 'public')));

const pool = mysql.createPool({
    host: 'contenedor-bdd',
    user: 'root',
    password: 'password',
    database: 'cine',
    charset: 'utf8mb4'
});


app.get('/peliculas', async (req, res) => {
    try {
        const [results] = await pool.query('SELECT * FROM peliculas');
        res.json(results);
        console.log("Registros Recibidos...");
    } catch (err) {
        console.error(err);
        res.status(500).json({error: 'Error al obtener los datos'});
    }
});


app.get('/peliculas/:id', async (req, res) => {
    try {
        const {id} = req.params;
        const [results] = await pool.query('SELECT * FROM peliculas WHERE id = ?', [id]);

        if (results.length === 0) {
            return res.status(404).json({mensaje: 'Pelicula no encontrada'});
        }
        res.json(results[0]);
        console.log(`Película Recibida con id ${id}`);
    } catch (err) {
        console.error(err);
        res.status(500).json({error: 'Error al obtener la película'});
    }
});


app.post('/peliculas', async (req, res) => {
    try {
        const { titulo, director, anio, genero } = req.body;
        const sql = `
            INSERT INTO peliculas (titulo, director, anio, genero)
            VALUES (?, ?, ?, ?)
        `;

        const [results] = await pool.query(sql, [
            titulo,
            director,
            anio,
            genero
        ]);

        res.status(201).json({
            mensaje: 'Película creada',
            id: results.insertId
        });
        console.log("Película creada exitosamente");

    } catch (err) {
        console.error(err);
        res.status(500).json({
            error: 'Error al crear la película'
        });
    }
});

app.put('/peliculas/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const { titulo, director, anio, genero } = req.body;

        const sql = `
            UPDATE peliculas
            SET titulo = ?, director = ?, anio = ?, genero = ?
            WHERE id = ?
        `;

        const [results] = await pool.query(sql, [
            titulo,
            director,
            anio,
            genero,
            id
        ]);

        if (results.affectedRows === 0) {
            return res.status(404).json({
                mensaje: 'Película no encontrada'
            });
        }

        res.json({
            mensaje: 'Película actualizada'
        });

        console.log("Película actualizada");

    } catch (err) {
        console.error(err);

        res.status(500).json({
            error: 'Error al actualizar la película'
        });
    }
});

app.delete('/peliculas/:id', async (req, res) => {
    try {
        const {id} = req.params;
        const [results] = await pool.query('DELETE FROM peliculas WHERE id = ?', [id]);

        if (results.affectedRows === 0) {
            return res.status(404).json({mensaje: 'Película no encontrada'});
        }
        res.json({mensaje: 'Película eliminada'});
        console.log(`Película con id ${id} eliminada`)
    } catch (err) {
        console.error(err);
        res.status(500).json({error: 'Error al borrar'});
    }
});

app.listen(port, () => {
    console.log(`Server Listening: http://localhost:${port}`);
});