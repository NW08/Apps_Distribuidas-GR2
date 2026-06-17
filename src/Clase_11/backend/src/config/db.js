const mysql = require('mysql2/promise');

const DB_CONFIG = {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    port: process.env.DB_PORT,
    password: process.env.DB_PASS,
    database: process.env.DB_NAME,
    connectionLimit: 10,
};

const RETRY_CONFIG = {
    max: 3,
    intervalMs: 5000,
};

/** @type {import('mysql2/promise').Pool} */
const pool = mysql.createPool(DB_CONFIG);

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function connectWithRetry() {

    for (let attempt = 1; attempt <= RETRY_CONFIG.max; attempt++) {
        try {
            const connection = await pool.getConnection();
            console.log('MySQL connection established successfully.');

            connection.release();
            return;
        } catch (error) {
            console.error(`Connection error (Attempt ${attempt}/${RETRY_CONFIG.max}): ${error.message}`);

            if (attempt < RETRY_CONFIG.max) {
                console.log(`Retrying in ${RETRY_CONFIG.intervalMs / 1000}s...`);
                await delay(RETRY_CONFIG.intervalMs);
            } else {
                console.error('Critical failure: Database unreachable after maximum retries.');
                process.exit(1);
            }
        }
    }
}

module.exports = {pool, connectWithRetry};