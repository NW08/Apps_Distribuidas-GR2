# ─────────────────────────────────────────────────────────────────
# Dockerfile — Backend Node.js + Express
# Puerto expuesto: 7001
#
# Comando para construir la imagen:
#   docker build -t cursos-backend .
#
# Comando para arrancar el contenedor en la red clone-replicacion:
#   docker run -d \
#     --name cursos-backend \
#     --network clone-replicacion \
#     -p 7001:7001 \
#     -e DB_HOST=172.18.0.2 \
#     -e DB_USER=root \
#     -e DB_PASS=tu_password \
#     -e DB_NAME=cursos-programDB \
#     -e JWT_SECRET=tu_secreto_jwt \
#     -e PORT=3001 \
#     cursos-backend

# MIRAR LAS IPs DE LOS CONTENEDORES QUE HICIMOS
# ─────────────────────────────────────────────────────────────────

FROM node:26

WORKDIR /app

# Copiar manifiestos e instalar solo dependencias de producción
COPY package*.json ./
RUN npm ci --only=production

# Copiar código fuente
COPY src/ ./src/

EXPOSE 7001

CMD ["node", "src/app.js"]
