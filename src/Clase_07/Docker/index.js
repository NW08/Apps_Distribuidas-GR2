const http = require('http');

const server = http.createServer((req, res) => {
    res.writeHead(200, {'Content-Type': 'text/plain'});
    res.end('¡Hola desde Node.js en Docker!\n');
});
server.listen(7000, () => {
    console.log('Servidor corriendo en http://localhost:7000');
});