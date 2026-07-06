const express = require('express');
const db = require('../db/database');
const { authMiddleware } = require('../middleware/auth');

const router = express.Router();

let messaging = null;
try {
  const { initializeApp, cert, getApps } = require('firebase-admin/app');
  const { getMessaging } = require('firebase-admin/messaging');
  
  const raw = process.env.FIREBASE_SERVICE_ACCOUNT;
  if (!raw) throw new Error('FIREBASE_SERVICE_ACCOUNT no definida');
  const serviceAccount = JSON.parse(raw);
  serviceAccount.private_key = serviceAccount.private_key.replace(/\\n/g, '\n');
  
  if (getApps().length === 0) {
    initializeApp({ credential: cert(serviceAccount) });
  }
  
  messaging = getMessaging();
  console.log('Firebase Admin inicializado correctamente');
} catch (e) {
  console.warn('Firebase Admin no configurado. Error:', e.message);
  messaging = null;
}

// Función para enviar notificación FCM
async function sendNewItemNotification(item) {
  if (!messaging) {
    console.log('FCM omitido: messaging no inicializado');
    return;
  }
  try {
    await messaging.send({
      notification: {
        title: '¡Nuevo objeto encontrado!',
        body: `${item.title} encontrado en ${item.location}`
      },
      topic: 'new_items'
    });
    console.log('Notificación FCM enviada');
  } catch (error) {
    console.error('Error enviando notificación FCM:', error.message);
  }
}

// GET /items
router.get('/', (req, res) => {
  try {
    const { category, type } = req.query;

    let query = 'SELECT * FROM items WHERE 1=1';
    const params = [];

    if (category) {
      query += ' AND category = ?';
      params.push(category);
    }

    if (type) {
      query += ' AND type = ?';
      params.push(type);
    }

    query += ' ORDER BY timestamp DESC';

    const items = db.prepare(query).all(...params);
    res.json(items);
  } catch (error) {
    console.error('Error en GET /items:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /items
router.post('/', authMiddleware, async (req, res) => {
  try {
    const { title, description, category, imageUrl, location, type } = req.body;

    if (!title || !description || !category || !location) {
      return res.status(400).json({
        error: 'Faltan campos requeridos: title, description, category, location'
      });
    }

    const itemType = type === 'lost' ? 'lost' : 'found';
    const timestamp = Date.now();

    const result = db.prepare(
      `INSERT INTO items (title, description, category, imageUrl, location, foundById, foundByEmail, status, type, timestamp)
       VALUES (?, ?, ?, ?, ?, ?, ?, 'available', ?, ?)`
    ).run(title, description, category, imageUrl || '', location, req.user.id, req.user.email, itemType, timestamp);

    const newItem = db.prepare('SELECT * FROM items WHERE id = ?').get(result.lastInsertRowid);

    res.status(201).json(newItem);

    if (itemType === 'found') {
      sendNewItemNotification(newItem).catch(err =>
        console.error('Error FCM:', err.message)
      );
    }

  } catch (error) {
    console.error('Error en POST /items:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// GET /items/:id
router.get('/:id', (req, res) => {
  try {
    const item = db.prepare('SELECT * FROM items WHERE id = ?').get(req.params.id);
    if (!item) {
      return res.status(404).json({ error: 'Objeto no encontrado' });
    }
    res.json(item);
  } catch (error) {
    console.error('Error en GET /items/:id:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /items
router.post('/', authMiddleware, async (req, res) => {
  try {
    const { title, description, category, imageUrl, location, type } = req.body;

    if (!title || !description || !category || !location) {
      return res.status(400).json({
        error: 'Faltan campos requeridos: title, description, category, location'
      });
    }

    const itemType = type === 'lost' ? 'lost' : 'found';
    const timestamp = Date.now();

    const result = db.prepare(
      `INSERT INTO items (title, description, category, imageUrl, location, foundById, foundByEmail, status, type, timestamp)
       VALUES (?, ?, ?, ?, ?, ?, ?, 'available', ?, ?)`
    ).run(title, description, category, imageUrl || '', location, req.user.id, req.user.email, itemType, timestamp);

    const newItem = db.prepare('SELECT * FROM items WHERE id = ?').get(result.lastInsertRowid);

    res.status(201).json(newItem);

    if (itemType === 'found') {
      sendNewItemNotification(newItem).catch(err =>
        console.error('Error FCM:', err.message)
      );
    }

  } catch (error) {
    console.error('Error en POST /items:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// PATCH /items/:id/status
router.patch('/:id/status', authMiddleware, (req, res) => {
  try {
    const { status } = req.body;
    if (!status || !['available', 'claimed'].includes(status)) {
      return res.status(400).json({ error: "status debe ser 'available' o 'claimed'" });
    }
    const item = db.prepare('SELECT * FROM items WHERE id = ?').get(req.params.id);
    if (!item) {
      return res.status(404).json({ error: 'Objeto no encontrado' });
    }
    db.prepare('UPDATE items SET status = ? WHERE id = ?').run(status, req.params.id);
    const updated = db.prepare('SELECT * FROM items WHERE id = ?').get(req.params.id);
    res.json(updated);
  } catch (error) {
    console.error('Error en PATCH /items/:id/status:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// DELETE /items/:id
router.delete('/:id', authMiddleware, (req, res) => {
  try {
    const item = db.prepare('SELECT * FROM items WHERE id = ?').get(req.params.id);
    if (!item) {
      return res.status(404).json({ error: 'Objeto no encontrado' });
    }
    if (item.foundById !== req.user.id) {
      return res.status(403).json({ error: 'No tienes permiso para eliminar este objeto' });
    }
    db.prepare('DELETE FROM items WHERE id = ?').run(req.params.id);
    res.status(204).send();
  } catch (error) {
    console.error('Error en DELETE /items/:id:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

module.exports = router;