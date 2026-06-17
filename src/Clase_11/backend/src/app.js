require('dotenv/config');

// 1. Fail-Fast: Environment variable validation
const REQUIRED_ENV = ['DB_HOST', 'DB_USER', 'DB_PASS', 'DB_NAME', 'JWT_SECRET', 'PORT'];
const missingEnv = REQUIRED_ENV.filter((key) => !process.env[key]);

if (missingEnv.length > 0) {
    console.error(`[Fatal Error] Missing mandatory environment variables: ${missingEnv.join(', ')}`);
    process.exit(1);
}

const express = require('express');
const cors = require('cors');
const {connectWithRetry} = require('./config/db');

const app = express();

// Global Middlewares
app.use(cors());
app.use(express.json());

// 3. Route Imports
const authRoutes = require('./routes/authRoutes');
const courseRoutes = require('./routes/cursoRoutes');
const userRoutes = require('./routes/usuarioRoutes');
const adminRoutes = require('./routes/adminRoutes');

// Route Mounting
app.use('/api/auth', authRoutes);
app.use('/api/cursos', courseRoutes);
app.use('/api/usuario', userRoutes);
app.use('/api/admin', adminRoutes);

// 4. Global Error Handling Middleware
app.use((err, req, res, next) => {
    console.error(`[Unhandled Error]: ${err.stack}`);
    res.status(500).json({error: 'Internal Server Error'});
});

// Fallback port added just in case
const PORT = process.env.PORT || 3000;

// 5. Modern Server Initialization using async/await
const startServer = async () => {
    await connectWithRetry();

    app.listen(PORT, () => {
        console.log(`Server running on port ${PORT}`);
    });
};

startServer().catch(err => {
    console.error(`[Startup Error]: ${err.message}`);
});

module.exports = app;