const express = require('express');
const db = require('../db/database');
const { authMiddleware } = require('../middleware/auth');

const router = express.Router();

// Inicializar Firebase Admin
let admin;
try {
  admin = require('firebase-admin');
  const raw = process.env.FIREBASE_SERVICE_ACCOUNT;
  if (!raw) throw new Error('FIREBASE_SERVICE_ACCOUNT no definida');
  const serviceAccount = JSON.parse(raw);
  serviceAccount.private_key = serviceAccount.private_key.replace(/\\n/g, '\n');
  if (!admin.apps.length) {
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount)
    });
    console.log('Firebase Admin inicializado correctamente');
  }
} catch (e) {
  console.warn('Firebase Admin no configurado:', e.message);
  admin = null;
}

// Función para enviar notificación FCM
async function sendNewItemNotification(item) {
  if (!admin) {
    console.log('FCM omitido: admin no inicializado');
    return;
  }
  try {
    await admin.messaging().send({
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
    const { category } = req.query;
    let items;
    if (category) {
      items = db.prepare('SELECT * FROM items WHERE category = ? ORDER BY timestamp DESC').all(category);
    } else {
      items = db.prepare('SELECT * FROM items ORDER BY timestamp DESC').all();
    }
    res.json(items);
  } catch (error) {
    console.error('Error en GET /items:', error);
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
    const { title, description, category, imageUrl, location } = req.body;

    if (!title || !description || !category || !location) {
      return res.status(400).json({
        error: 'Faltan campos requeridos: title, description, category, location'
      });
    }

    const timestamp = Date.now();

    const result = db.prepare(
      `INSERT INTO items (title, description, category, imageUrl, location, foundById, foundByEmail, status, timestamp)
       VALUES (?, ?, ?, ?, ?, ?, ?, 'available', ?)`
    ).run(title, description, category, imageUrl || '', location, req.user.id, req.user.email, timestamp);

    const newItem = db.prepare('SELECT * FROM items WHERE id = ?').get(result.lastInsertRowid);

    // Responder primero, luego notificar
    res.status(201).json(newItem);

    // Enviar notificación sin bloquear la respuesta
    sendNewItemNotification(newItem).catch(err =>
      console.error('Error FCM:', err.message)
    );

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