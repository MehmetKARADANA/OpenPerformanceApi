const amqp = require('amqplib/callback_api');

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

    const QUEUE_NAME = 'task_queue';

    // Kuyruğu oluştur
    channel.assertQueue(QUEUE_NAME, { durable: true });
    channel.prefetch(500); // Aynı anda bir işlem yap

    console.log(`Waiting for messages in ${QUEUE_NAME}. To exit press CTRL+C`);

    // Kuyruktan gelen mesajları işleme
    channel.consume(QUEUE_NAME, (msg) => {
      if (msg !== null) {
        const message = JSON.parse(msg.content.toString());
        console.log('Processing message:', message);

        // Gecikme simülasyonu (örneğin, her isteğe 1 saniye gecikme ekleyebiliriz)
        setTimeout(() => {
          console.log('Done processing message:', message);
          channel.ack(msg); // Mesajı başarıyla işaretle
        }, 1000); // Simüle edilen işlem süresi
      }
    }, { noAck: false });
  });
});
