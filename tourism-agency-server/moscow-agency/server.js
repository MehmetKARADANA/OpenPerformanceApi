const express = require('express');
const amqp = require('amqplib/callback_api');
const app = express();
const port = 3005;
const distance_km = 3000;
const delay = distance_km / 10;

const QUEUE_NAME = 'task_queue';

// RabbitMQ bağlantısını açma
amqp.connect('amqp://localhost', (err, connection) => {
  if (err) {
    console.error('Failed to connect to RabbitMQ', err);
    return;
  }

  connection.createChannel((err, channel) => {
    if (err) {
      console.error('Failed to create channel', err);
      return;
    }

    // Kuyruğu oluştur
    channel.assertQueue(QUEUE_NAME, { durable: true });
    console.log(`Waiting for messages in ${QUEUE_NAME}. To exit press CTRL+C`);

    // Express.js ile gelen istekleri kuyruğa yönlendirme
    app.use((req, res, next) => {
      setTimeout(next, delay); // Gecikmeyi simüle et
    });

    app.get('/rooms', (req, res) => {
      // İstek verilerini JSON formatında kuyruğa gönder
      const message = {
        url: req.originalUrl,
        method: req.method,
        agency: 'Moscow Agency',
        rooms: [
          { id: 1, name: 'Kremlin Suite', price: 450, available: true },
          { id: 2, name: 'Red Square Room', price: 200, available: true },
          { id: 3, name: 'Economy Room', price: 70, available: false }
        ]
      };

      // Kuyruğa mesaj gönder
      channel.sendToQueue(QUEUE_NAME, Buffer.from(JSON.stringify(message)), { persistent: true });
      console.log('Message sent to queue:', message);

      // İstek hemen yanıtlanır
      res.send('Request queued for processing');
    });

    // Sunucu başlatma
    app.listen(port, () => {
      console.log(`Moscow Agency server running at http://localhost:${port}`);
    });
  });
});
