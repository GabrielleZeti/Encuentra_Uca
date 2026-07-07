const { Pool } = require('pg');

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false
});

async function initializeDatabase() {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS users (
      id SERIAL PRIMARY KEY,
      name TEXT NOT NULL,
      email TEXT NOT NULL UNIQUE,
      password TEXT NOT NULL,
      "createdAt" BIGINT NOT NULL
    )
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS items (
      id SERIAL PRIMARY KEY,
      title TEXT NOT NULL,
      description TEXT NOT NULL,
      category TEXT NOT NULL,
      "imageUrl" TEXT,
      location TEXT NOT NULL,
      "foundById" INTEGER NOT NULL,
      "foundByEmail" TEXT NOT NULL,
      status TEXT NOT NULL DEFAULT 'available',
      type TEXT NOT NULL DEFAULT 'found',
      timestamp BIGINT NOT NULL,
      FOREIGN KEY ("foundById") REFERENCES users(id)
    )
  `);

  console.log('Base de datos PostgreSQL inicializada');
}

initializeDatabase().catch(console.error);

module.exports = pool;