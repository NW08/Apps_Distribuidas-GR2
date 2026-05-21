# Guía de Inicio | PARTE 2 — Interfaz Web CRUD con HTML + CSS

> **Continuación.** Esta guía parte directamente del entorno configurado en el paso anterior: el contenedor MySQL corriendo y la
> carpeta `web` con su `index.js` y dependencias ya instaladas. Aquí se añade una interfaz visual desde la cual podrás ver, crear,
> editar y eliminar registros de la base de datos sin tocar la terminal.

---

## Antes de empezar

Verifica que tienes el entorno del paso anterior funcionando:

```bash
docker ps -a
```

> El contenedor `mi-contenedor` debe aparecer con `STATUS: Up`. Si está detenido, inícialo con `docker start mi-contenedor`.

---

## Paso 1 — Actualizar el servidor

Reemplaza el contenido de tu archivo `web/index.js` actual con el del repositorio, que incorpora los nuevos endpoints para las
operaciones de creación, edición y eliminación:

[`web/index.js`](./index.js)

---

## Paso 2 — Crear la interfaz HTML

Dentro de la carpeta `web`, crea una carpeta llamada `public` y dentro de ella un archivo `index.html`:

```bash
mkdir public
```

Copia el contenido del siguiente archivo del repositorio en `web/public/index.html`:

[`web/public/index.html`](./public/index.html)

---

## Paso 3 — Iniciar la aplicación

Muévete adentro de la carpeta `web`, ejecuta:

```bash
node index.js
```

```
Server Listening: http://localhost:7000
```

Abre `http://localhost:7000` en tu navegador. Deberías ver la interfaz con los registros de la base de datos cargados.

---

## Qué puedes hacer desde la interfaz

| Operación    | Descripción                                                             |
|--------------|-------------------------------------------------------------------------|
| **Ver**      | Los registros existentes se listan automáticamente al cargar la página. |
| **Crear**    | Usa el formulario para añadir un nuevo registro a la base de datos.     |
| **Editar**   | Selecciona un registro existente y modifica sus campos directamente.    |
| **Eliminar** | Elimina un registro de forma permanente desde la misma interfaz.        |

---

## Estructura de carpetas resultante

```
📁 tu-carpeta-del-proyecto/
├── 📁 tu-carpeta-de-volúmenes/
│   ├── empleados.sql
│   └── my.cnf
└── 📁 web/
    ├── 📁 public/
    │   └── index.html
    ├── index.js
    └── package.json
```

---

> **Siguiente paso →** En la [`carpeta final`](../_3_Full_Docker) del repositorio se contenerizará la aplicación `web` junto con la base de
> datos, conectando ambos servicios mediante una red Docker para que todo el sistema corra sin depender de la máquina local.