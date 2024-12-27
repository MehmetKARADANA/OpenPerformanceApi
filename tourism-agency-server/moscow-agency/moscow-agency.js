const express = require('express');
const app = express();
const port = 3005;
const distance_km=3000;
const delay = distance_km / 10;

//app.use((req, res, next) => setTimeout(next, delay))
app.get('/rooms', (req, res) => {
  res.json({
    agency: 'Moscow Agency',
    rooms: [
      { id: 1, name: 'Kremlin Suite', price: 450, available: true },
      { id: 2, name: 'Red Square Room', price: 200, available: true },
      { id: 3, name: 'Economy Room', price: 70, available: false }
    ]
  });
});

app.listen(port, () => {
  console.log(`Moscow Agency server running at http://localhost:${port}`);
});/*

const express = require('express');
const cluster = require('cluster');
const numCPUs = require('os').cpus().length;
const process = require('process');

const distance_km = 3000;
const delay = distance_km / 10; // 800 milisaniye
const port = 3005;

if (cluster.isPrimary) {
    console.log(`Primary ${process.pid} is running`);
    for (let i = 0; i < numCPUs; i++) {
        cluster.fork();
    }
    cluster.on('exit', (worker, code, signal) => {
        console.log(`worker ${worker.process.pid} died`);
        cluster.fork();
    });
} else {
    const app = express();

    const compression = require('compression');
    app.use(compression());

    const helmet = require('helmet');
    app.use(helmet());

    const apicache = require('apicache').middleware;
    let cache = apicache('5 minutes');
    app.use('/rooms', cache);

    app.use(express.static('public', { maxAge: '1d', immutable: true }));
    app.use(express.json({ limit: '1mb' }));

    app.get('/rooms', (req, res) => {
        setTimeout(() => {
            res.json({
                         agency: 'Moscow Agency',
                         rooms: [
                           { id: 1, name: 'Kremlin Suite', price: 450, available: true },
                           { id: 2, name: 'Red Square Room', price: 200, available: true },
                           { id: 3, name: 'Economy Room', price: 70, available: false }
                         ]
                       });
        }, delay); // Belirtilen gecikme kadar bekle
    });

    app.get('/health', (req, res) => {
        res.status(200).send('OK');
    });

    app.use((req, res, next) => {
        res.status(404).send("Sorry, can't find that resource!");
    });

    app.use((err, req, res, next) => {
        console.error(err.stack);
        res.status(500).send('Something broke!');
    });

    const server = app.listen(port, () => {
        console.log(`Worker ${process.pid} started, listening on port ${port}`);
    });

    server.timeout = 120000;
    server.keepAliveTimeout = 65000;
}*/