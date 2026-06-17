# ─────────────────────────────────────────────────────────────────
# Dockerfile — Frontend React + Tailwind CSS (multi-stage)
# Stage 1: build con Node.js
# Stage 2: serve con nginx en puerto 80
#
# Comando para construir la imagen:
#   docker build -t cursos-frontend .
#
# Comando para arrancar el contenedor en la red clone-replicacion:
#   docker run -d \
#     --name cursos-frontend \
#     --network clone-replicacion \
#     -p 80:80 \
#     cursos-frontend
# ─────────────────────────────────────────────────────────────────

# ── Stage 1: Build ─────────────────────────────────────────────
FROM node:26 AS build

WORKDIR /app

# Instalar dependencias primero (capa cacheada)
COPY package*.json ./
RUN npm ci

# Copiar código fuente y archivos de configuración
COPY index.html ./
COPY vite.config.js ./
COPY tailwind.config.js ./
COPY postcss.config.js ./
COPY src/ ./src/

# Compilar la aplicación React
RUN npm run build

# ── Stage 2: Serve ─────────────────────────────────────────────
FROM nginx:alpine

# Copiar los archivos estáticos compilados
COPY --from=build /app/dist /usr/share/nginx/html

# Copiar configuración nginx con SPA fallback
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
