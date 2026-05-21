# MySQL en Docker + App Node.js CRUD || `CLASE 08 (20|05|2026)`

Se hizo este proyecto como continuación práctica a la introducción de Docker, escalando de un contenedor simple a un sistema de
dos servicios interconectados: una base de datos **MySQL** corriendo en un contenedor con persistencia de datos mediante volúmenes,
y una aplicación **Node.js + Express** con interfaz web que realiza operaciones CRUD contra esa base de datos. Ambos servicios
se comunican a través de una red **red-node** de Docker, sin depender del entorno local de la máquina.

## Vista General del Proyecto

El proyecto está organizado en tres etapas progresivas, cada una documentada en su propia guía. La primera levanta el contenedor
MySQL, crea la base de datos y conecta la app localmente. La segunda añade una interfaz visual HTML para operar el CRUD desde el
navegador. La tercera conteneriza la app Node.js y conecta ambos servicios exclusivamente por red Docker, cerrando el ciclo.
Construido con **Node.js**, **Express** y **MySQL2**, gestionado con **Docker Engine** sin Docker Compose de forma intencional
para exponer el funcionamiento interno de redes, volúmenes y contenedores.

---

### Infraestructura Docker

| Recurso         | Responsabilidad                                                                                                      |
|-----------------|----------------------------------------------------------------------------------------------------------------------|
| `mi-contenedor` | Contenedor MySQL. Expone el puerto `3306`, persiste datos en el volumen `mi-vol` y corre en la red `red-node`.       |
| `node-crud`     | Contenedor Node.js. Expone el puerto `7000`, se comunica con `mi-contenedor` por nombre dentro de la red `red-node`. |
| `mi-vol`        | Volumen Docker que persiste los datos de MySQL independientemente del ciclo de vida del contenedor.                  |
| `empleados.sql` | Script SQL que crea y puebla la base de datos `base_empleados` con los registros iniciales.                          |
| `my.cnf`        | Archivo de configuración de MySQL montado como volumen para personalizar el comportamiento del servidor.             |

### Módulo `web` (App Node.js)

| Archivo             | Responsabilidad                                                                                                              |
|---------------------|------------------------------------------------------------------------------------------------------------------------------|
| `index.js`          | Servidor Express. Expone los endpoints REST para las operaciones CRUD y sirve los archivos estáticos de la carpeta `public`. |
| `public/index.html` | Interfaz web HTML + CSS desde la cual el usuario puede ver, crear, editar y eliminar registros de la base de datos.          |
| `Dockerfile`        | Define la imagen personalizada de la app: imagen base `node:alpine`, instalación de dependencias y comando de arranque.      |
| `package.json`      | Manifiesto del proyecto. Declara las dependencias `express` y `mysql2`.                                                      |

---

## Guías Paso a Paso

Este proyecto se desarrolla en tres etapas. Cada guía parte del estado final de la anterior:

| Etapa | Guía                                                                | Qué se logra                                                                  |
|-------|---------------------------------------------------------------------|-------------------------------------------------------------------------------|
| 1     | [MySQL en Docker + App Local](../../src/Clase_08/_1_Primeros_Pasos) | Contenedor MySQL, volumen, base de datos poblada y app corriendo localmente.  |
| 2     | [Interfaz CRUD HTML + CSS](../../src/Clase_08/_2_CRUD)              | Interfaz web para ver, crear, editar y eliminar registros desde el navegador. |
| 3     | [Contenerizar la App Node.js](../../src/Clase_08/_3_Full_Docker)    | App empaquetada en contenedor y ambos servicios conectados por red Docker.    |

---

## Características Implementadas

- **Sistema Multi-Contenedor Manual:** Dos contenedores independientes (`mysql` y `node`) conectados por red Docker `red-node`
  sin usar Docker Compose, exponiendo explícitamente cómo Docker resuelve nombres de contenedor como direcciones IP dentro de
  una red compartida.
- **Persistencia con Volúmenes:** Los datos de MySQL sobreviven al ciclo de vida del contenedor gracias al volumen `mi-vol`
  montado en `/var/lib/mysql`, garantizando que no se pierda información al detener o recrear el contenedor.
- **API REST con Express:** El servidor Node.js expone endpoints para cada operación CRUD, separando la lógica del servidor
  de la presentación en el cliente HTML.
- **Interfaz Web Desacoplada:** La carpeta `public/` sirve archivos estáticos directamente desde Express, manteniendo el
  frontend separado de la lógica de negocio del servidor.
- **Comunicación entre Contenedores por Nombre:** Al cambiar `host: 'localhost'` por `host: 'mi-contenedor'` en el pool de
  conexión, la app resuelve la dirección del servicio MySQL a través del DNS interno de la red Docker.
- **Imagen Optimizada por Capas:** El `Dockerfile` de la app copia y resuelve dependencias antes de copiar el código fuente,
  aprovechando la caché de capas de Docker para acelerar reconstrucciones posteriores.

---

## Estructura Final del Proyecto

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

## Resumen de Contenedores

| Contenedor      | Imagen         | Puerto        | Red        | Rol                     |
|-----------------|----------------|---------------|------------|-------------------------|
| `mi-contenedor` | `mysql:latest` | `3306 → 3306` | `red-node` | Base de datos (MySQL)   |
| `node-crud`     | `crud-app:1`   | `7000 → 7000` | `red-node` | Servidor web + API REST |

---

## Conceptos Clave

| Concepto            | Descripción                                                                                                                            |
|---------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `Volumen Docker`    | Mecanismo de persistencia gestionado por Docker. Los datos sobreviven al contenedor y pueden compartirse entre varios.                 |
| `Red bridge`        | Red virtual privada por defecto de Docker. Los contenedores en la misma red se comunican entre sí usando su nombre como hostname.      |
| `docker exec`       | Ejecuta un comando dentro de un contenedor en ejecución. Usado para acceder al bash y correr el script SQL.                            |
| `docker cp`         | Copia archivos entre la máquina host y el sistema de archivos de un contenedor.                                                        |
| `docker logs`       | Muestra la salida estándar del proceso principal de un contenedor, útil para monitorear la actividad en tiempo real.                   |
| `docker build`      | Lee el `Dockerfile` y construye una nueva imagen local, identificándola con un tag (`-t nombre:versión`).                              |
| `Port Binding (-p)` | Mapea un puerto del host a un puerto del contenedor con el formato `-p <host>:<contenedor>`, exponiendo el servicio al exterior.       |
| `PreparedStatement` | Patrón usado en `mysql2` para parametrizar consultas SQL, previniendo inyección SQL al separar estructura y datos.                     |
| **Docker Compose**  | *Próximos proyectos:* herramienta que define y orquesta múltiples contenedores, redes y volúmenes desde un único `docker-compose.yml`. |