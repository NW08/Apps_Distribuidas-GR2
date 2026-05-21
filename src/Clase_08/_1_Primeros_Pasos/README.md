# Guía de Inicio | PARTE 1 — MySQL en Docker + App Node.js

> **Punto de partida.** Esta guía cubre la configuración base del entorno: levantar una base de datos MySQL dentro de un contenedor
> Docker, poblarla con datos reales y conectarla a una aplicación Node.js, primero corriendo localmente y luego contenerizada.
> Las carpetas siguientes del repositorio construirán directamente sobre lo que se deja listo aquí.

---

## Antes de empezar

Asegúrate de tener lo siguiente instalado en tu máquina:

- **Docker Desktop** — [Descarga Aquí](https://www.docker.com/products/docker-desktop/)
- **Node.js 18 LTS o superior** — [Descarga Aquí](https://nodejs.org/)
- Un cliente MySQL opcional para inspección visual, como [DBeaver](https://dbeaver.io/) o [TablePlus](https://tableplus.com/)

---

## Paso 1 — Descargar la imagen de MySQL

Descarga la imagen oficial desde Docker Hub y verifica que quedó disponible localmente:

```bash
docker pull mysql:latest
```

```bash
docker images
```

> Deberías ver `mysql:latest` listada con su ID y tamaño (~1.3 GB).

---

## Paso 2 — Crear la carpeta del proyecto y el volumen de persistencia

Crea una carpeta para el proyecto. Dentro de ella crea una carpeta para los volúmenes y dentro de ella ejecuta los siguientes comandos para
crear un **volumen Docker** que persistirá los datos de la base de datos aunque el contenedor se detenga o elimine:

```bash
docker volume create mi-vol
```

Verifica que el volumen fue creado correctamente:

```bash
docker volume ls
```

> Deberías ver `mi-vol` listado bajo el driver `local`.

---

## Paso 3 — Crear y levantar el contenedor de MySQL

Ejecuta el siguiente comando para crear el contenedor, mapeando el puerto y el volumen. Reemplaza `"tu usuario"` y `"ruta a tu carpeta"`
con los valores reales de tu máquina:

```bash
docker run -d --name mi-contenedor --network bridge \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_ROOT_HOST=% \
  -v mi-vol:/var/lib/mysql \
  -v C:\Users\"tu usuario"\"ruta a tu carpeta de volúmenes"\my.cnf:/etc/mysql/my.cnf \
  -p 3306:3306 \
  mysql:latest
```

Verifica que el contenedor quedó corriendo:

```bash
docker ps -a
```

> El campo `STATUS` debe mostrar `Up` con el tiempo transcurrido desde que inició.

---

## Paso 4 — Copiar el script SQL al contenedor

Descarga el archivo [`empleados.sql`](../empleados.sql) y muévelo dentro de tu carpeta de volúmenes.
Luego cópialo al interior del contenedor con:

```bash
docker cp "ruta de tu carpeta de volúmenes"\empleados.sql mi-contenedor:/empleados.sql
```

> Deberías ver: `Successfully copied ... to mi-contenedor:/empleados.sql`

---

## Paso 5 — Crear la base de datos dentro del contenedor

Accede al bash del contenedor:

```bash
docker exec -it mi-contenedor bash
```

Ya dentro, conéctate a MySQL con el usuario root:

```bash
mysql -u root -p
```

Cuando se solicite, ingresa la contraseña: `password`

Una vez dentro de la consola MySQL, ejecuta el script para crear y poblar la base de datos:

```sql
SOURCE /empleados.sql;
```

> Deberías ver una serie de líneas `Query OK` confirmando la creación de la tabla e inserción de los registros.

---

## Paso 6 — Verificar los datos

Consulta las tablas disponibles en la base de datos:

```sql
SHOW TABLES;
```

Con el nombre de la tabla que aparezca, ejecuta:

```sql
SELECT *
FROM nombre_de_la_tabla;
```

> Si ves los registros listados, la base de datos está lista. Puedes escribir `exit` para salir de MySQL y `exit` nuevamente para
> salir del bash del contenedor.

---

## Paso 7 — Crear la aplicación Node.js

En la raíz del proyecto, al mismo nivel que la carpeta de volúmenes, crea una carpeta llamada `web`. Ábrela en el terminal y
ejecuta:

```bash
npm init -y
npm install express mysql2
```

---

## Paso 8 — Agregar el archivo de la aplicación

Dentro de la carpeta `web`, crea un archivo `index.js` y copia el contenido desde el repositorio:
[`web/index.js`](./index.js)

---

## Paso 9 — Ejecutar la app localmente

Con el contenedor de MySQL corriendo y desde dentro de la carpeta `web`, ejecuta:

```bash
node index.js
```

```
Server Listening: http://localhost:7000
```

Abre `http://localhost:7000` en tu navegador. Deberías ver los datos de la base de datos desplegados en pantalla.

> ⚠️ El contenedor de MySQL debe estar en estado `Up` para que la conexión funcione. Puedes verificarlo con `docker ps -a` en otra terminal.

---

## Estructura de carpetas resultante

```
📁 tu-carpeta-del-proyecto/
├── 📁 tu-carpeta-de-volúmenes/
│   ├── empleados.sql
│   └── my.cnf
└── 📁 web/
    ├── index.js
    └── package.json
```

---

> **Siguiente paso →** En la [`siguiente carpeta`](../_2_CRUD) se tomará esta base y se modificará la aplicación,
> de modo que tanto la base de datos como el servidor Node.js puedan realizar un CRUD de la información.
