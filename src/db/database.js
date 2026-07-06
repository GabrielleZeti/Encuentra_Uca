const Database = require('better-sqlite3');
const path = require('path');

const db = new Database(
  process.env.DB_PATH || path.join(__dirname, '..', '..', 'encuentra_uca.db')
);

db.pragma('journal_mode = WAL');

db.exec(`
  CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    createdAt INTEGER NOT NULL
  )
`);

db.exec(`
  CREATE TABLE IF NOT EXISTS items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL,
    imageUrl TEXT,
    location TEXT NOT NULL,
    foundById INTEGER NOT NULL,
    foundByEmail TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'available',
    type TEXT NOT NULL DEFAULT 'found',
    timestamp INTEGER NOT NULL,
    FOREIGN KEY (foundById) REFERENCES users(id)
  )
`);

module.exports = db;