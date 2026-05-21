const express = require('express');
const mysql = require('mysql2');

const app = express();
const port = 7000;

const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'password',
    database: 'base_empleados',
    charset: 'utf8mb4'
});

app.get('/', (req, res) => {

    connection.query('SELECT * FROM personal', (err, results) => {

        if (err) {
            console.error(err);
            res.status(500).send('Error en consulta SQL');
            return;
        }

        res.setHeader('Content-Type', 'application/json; charset=utf-8');
        res.json(results);
    });
});

app.listen(port, () => {
    console.log(`Server Listening: http://localhost:${port}`);
});