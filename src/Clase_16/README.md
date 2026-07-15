# Examen Bimestral #02 (Girón María & Ortiz Josué) || `CLASE 16 (15|07|2026)`

Construir una arquitectura distribuida para procesar las interacciones de 15 videos de una red social estudiantil.

**Interacciones disponibles:**

- `like`
- `comment`
- `shared`
- `view`

---

## Actividades

### 1. Base de datos

- Cargar el script SQL en un volumen Docker.
- Levantar la base de datos.

### 2. API Spring Boot

Crear un endpoint:

```http
GET /datos
```

La respuesta debe ser texto plano (no JSON), por ejemplo:

```text
Ana,view,2026-07-01,20:00:00,video1
```

Este formato será la entrada del proceso MapReduce.

### 3. Balanceador de carga

Configurar dos instancias de la aplicación (`server1` y `server2`) para que el endpoint responda desde cualquiera de ellas.

### 4. Procesamiento MapReduce

Calcular:

- Video con más vistas.
- Video con más likes.
- Video con más comentarios.
- Usuario con más interacciones.
- Hora con mayor actividad.
- Video con mayor ratio de interacción.

### Ratio de interacción

```text
(likes + comments + shares) / views
```

---

## Flujo del sistema

```text
Base de Datos
      │
      ▼
Spring Boot (GET /datos)
      │
      ▼
Balanceador de carga
   ┌──────────┐
   ▼          ▼
Server 1   Server 2
   └────┬─────┘
        ▼
    MapReduce
        ▼
  Métricas finales
```

---

## Entregables

- Informe con la arquitectura y funcionamiento del sistema.
- Resultados obtenidos.
- Conclusiones y recomendaciones.