# Guía de Inicio | PARTE 3 — Contenerizar la App Node.js y Conectarla a MySQL

> **Cierre del ciclo.** Esta es la última guía de la serie. Hasta ahora la app Node.js corría directamente en tu máquina local
> mientras la base de datos vivía en un contenedor. Aquí empaquetamos la app en su propio contenedor y conectamos ambos servicios
> a través de la red Docker, logrando que el sistema completo corra de forma aislada y sin depender del entorno local.

---

## Antes de empezar

Verifica que el contenedor de MySQL sigue corriendo:

```bash
docker ps -a
```

> `mi-contenedor` debe aparecer con `STATUS: Up`. Si está detenido, inícialo con `docker start mi-contenedor`.

---

## Paso 1 — Agregar el Dockerfile

Dentro de la carpeta `web`, crea un archivo llamado `Dockerfile` y copia su contenido desde el repositorio:

[`web/Dockerfile`](./Dockerfile)

---

## Paso 2 — Actualizar la conexión en `index.js`

Para que el contenedor Node.js pueda comunicarse con el de MySQL, deben estar en la **misma red Docker**. La comunicación
entre contenedores en una red bridge se hace por **nombre de contenedor**, no por `localhost`. Abre `web/index.js` y actualiza
el campo `host` del pool de conexión:

```javascript
const pool = mysql.createPool({
    host: 'mi-contenedor', // <- antes: 'localhost'
    user: 'root',
    password: 'password',
    database: 'base_empleados',
    charset: 'utf8mb4'
});
```

> Esto funciona porque ambos contenedores estarán en la red `red-node` que configuramos al crear el contenedor de MySQL.
> Docker resuelve el nombre `mi-contenedor` como dirección IP automáticamente dentro de esa red.

---

## Paso 3 — Construir la imagen de la app

Desde dentro de la carpeta `web`, ejecuta:

```bash
docker build -t crud-app:1 .
```

> Puedes reemplazar `crud-app` por el nombre que prefieras y `1` por la versión que quieras asignarle.
> Al finalizar deberías ver `Successfully built` y la nueva imagen listada en `docker images`.

---

## Paso 4 — Crear y ejecutar el contenedor de la app

```bash
docker run -d --name node-crud --network red-node -p 7000:7000 crud-app:1
```

> ⚠️ El flag `--network red-node` debe coincidir exactamente con la red del contenedor MySQL. Si usaste un nombre de red
> personalizado al crear `mi-contenedor`, reemplaza `red-node` por ese nombre. Sin red compartida, los contenedores no
> pueden verse entre sí y la app no podrá conectarse a la base de datos.

---

## Paso 5 — Verificar el sistema completo

Confirma que ambos contenedores están corriendo:

```bash
docker ps -a
```

> Deberías ver tanto `mi-contenedor` como `node-crud` con `STATUS: Up`.

Abre `http://localhost:7000` en tu navegador y comprueba que la interfaz carga y responde correctamente.

Finalmente, monitorea la actividad del contenedor Node.js en tiempo real mientras interactúas con la app:

```bash
docker logs node-crud
```

> Cada acción que realices en el navegador (crear, editar, eliminar) debería reflejarse como una entrada en los logs del contenedor.

---

## Estructura final del proyecto

```
📁 tu-carpeta-del-proyecto/
├── 📁 tu-carpeta-de-volúmenes/
│   ├── empleados.sql
│   └── my.cnf
└── 📁 web/
    ├── 📁 public/
    │   └── index.html
    ├── Dockerfile
    ├── index.js
    └── package.json
```

---

## Resumen de contenedores levantados

| Contenedor      | Imagen         | Puerto        | Red        | Rol                     |
|-----------------|----------------|---------------|------------|-------------------------|
| `mi-contenedor` | `mysql:latest` | `3306 → 3306` | `red-node` | Base de datos (MySQL)   |
| `node-crud`     | `crud-app:1`   | `7000 → 7000` | `red-node` | Servidor web + API REST |

---

> **Fin de la serie.** El sistema completo ahora corre en contenedores aislados que se comunican entre sí por red interna Docker.
> En los siguientes proyectos del repositorio se simplificará toda esta configuración con **Docker Compose**, definiendo ambos
> servicios, su red y volúmenes en un único archivo `docker-compose.yml`.