require('dotenv').config();
const express = require('express');
const cors = require('cors');

const authRoutes = require('./routes/auth');
const itemsRoutes = require('./routes/items');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Ruta de salud, útil para verificar que el servidor está vivo (Railway la usa)
app.get('/', (req, res) => {
  res.json({ status: 'ok', message: 'Encuentra UCA API funcionando' });
});

app.use('/auth', authRoutes);
app.use('/items', itemsRoutes);

// Manejo de rutas no encontradas
app.use((req, res) => {
  res.status(404).json({ error: 'Ruta no encontrada' });
});

app.listen(PORT, () => {
  console.log(`Servidor corriendo en puerto ${PORT}`);
});
