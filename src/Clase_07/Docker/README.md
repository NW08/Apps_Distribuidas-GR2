# Introducción a Docker - Contenerización de Aplicaciones || `CLASE 07 (13|05|2026)`

Se hizo este proyecto como práctica introductoria a **Docker** como plataforma de contenerización. Se exploraron los conceptos
fundamentales del ciclo de vida de imágenes y contenedores, y se culminó con el despliegue de una aplicación **Node.js** real dentro
de un contenedor, demostrando cómo aislar y portar una aplicación junto con todas sus dependencias de forma reproducible.

## Vista General del Proyecto

Este proyecto está dividido en dos partes prácticas y progresivas. La primera valida la instalación de Docker ejecutando la imagen
oficial `hello-world`. La segunda despliega un servidor HTTP minimalista en **Node.js** construyendo una imagen personalizada desde
un `Dockerfile`, mapeando puertos entre el contenedor y la máquina host para acceder a la aplicación desde el navegador.
Construido con **Node.js** y gestionado íntegramente con **Docker Engine**.

---

### Parte 1: Verificación del Entorno (`hello-world`)

| Comando / Recurso               | Responsabilidad                                                                                    |
|---------------------------------|----------------------------------------------------------------------------------------------------|
| `docker pull hello-world`       | Descarga la imagen oficial de Docker Hub para verificar que el entorno funciona correctamente.     |
| `docker create --name <nombre>` | Crea un contenedor a partir de la imagen descargada sin ejecutarlo aún.                            |
| `docker start <nombre>`         | Inicia el contenedor, ejecuta su proceso principal y muestra la salida de confirmación en consola. |
| `docker images`                 | Lista todas las imágenes disponibles localmente con su ID, tamaño en disco y estado de uso.        |
| `docker ps -a`                  | Lista todos los contenedores (activos y detenidos) con su estado, puertos y nombre asignado.       |

### Parte 2: App Node.js (crea una carpeta llamada `mi-app-docker`)

| Archivo / Recurso | Responsabilidad                                                                                                                           |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| `src/index.js`    | Servidor HTTP de Node.js. Escucha en el puerto `7000` y responde con un mensaje de texto plano a cualquier petición entrante.             |
| `Dockerfile`      | Define la imagen personalizada: imagen base, directorio de trabajo, copia de archivos, instalación de dependencias y comando de arranque. |
| `package.json`    | Manifiesto del proyecto Node.js generado por `npm init -y`. Declara el nombre, versión y punto de entrada del servicio.                   |

---

## Contenido de los Archivos

> Los archivos fuente se encuentran en el repositorio. Cópialos dentro de la estructura de carpetas del proyecto según la ruta indicada.

| Archivo                                                 | Ruta destino en el proyecto  |
|---------------------------------------------------------|------------------------------|
| [`index.js`](../../../src/Clase_07/Docker/index.js)     | `mi-app-docker/src/index.js` |
| [`Dockerfile`](../../../src/Clase_07/Docker/Dockerfile) | `mi-app-docker/Dockerfile`   |

---

## Características Implementadas

- **Ciclo de Vida Completo de un Contenedor:** Se practicaron los tres estados fundamentales: `pull` (descargar imagen), `create`
  (instanciar contenedor) y `start` (ejecutar). Esto diferencia explícitamente la *imagen* (plantilla inmutable) del *contenedor*
  (instancia en ejecución).
- **Imágenes Personalizadas con Dockerfile:** Se construyó una imagen propia usando `docker build`, siguiendo las buenas prácticas
  de capas: primero copiar y resolver dependencias (`package.json` + `npm install`) y luego copiar el código fuente, para aprovechar
  la caché de capas de Docker en reconstrucciones posteriores.
- **Mapeo de Puertos (Port Binding):** El flag `-p 7000:7000` en `docker run` conecta el puerto `7000` del contenedor con el
  puerto `7000` de la máquina host, permitiendo acceder al servicio desde el navegador en `http://localhost:7000`.
- **Imagen Base Minimalista:** El `Dockerfile` usa `node:alpine` como base, lo cual reduce drásticamente el tamaño de la imagen
  final al estar basada en Alpine Linux, una distribución de solo ~5 MB diseñada para contenedores.
- **Aislamiento de Dependencias:** El servidor Node.js y todas sus dependencias viven dentro del contenedor. La máquina host no
  necesita tener Node.js instalado para ejecutar la aplicación.

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta                                                       | Versión / Enlace                               |
|-------------------------------------------------------------------|------------------------------------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Última versión estable                         |
| [Node.js](https://nodejs.org/)                                    | 18 LTS o superior (solo para desarrollo local) |

> **Nota:** Docker Desktop incluye tanto el *Docker Engine* como la CLI. Una vez instalado, verífica que funciona ejecutando
> `docker --version` en una terminal. Node.js solo es necesario si deseas ejecutar el servidor fuera del contenedor.

### Pasos — Parte 1: Hello World

1. **Clona el repositorio** *(o crea la carpeta manualmente y copia los archivos)*
   ```bash
   git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
   cd src/Clase_07/Docker
   ```

2. **Descarga la imagen de prueba**
   ```bash
   docker pull hello-world:latest
   ```

3. **Crea el contenedor**
   ```bash
   docker create --name mi-contenedor hello-world
   ```

4. **Verifica la imagen y el contenedor creados**
   ```bash
   docker images
   docker ps -a
   ```

5. **Ejecuta el contenedor**
   ```bash
   docker start mi-contenedor
   ```
   *Salida esperada:* `Hello from Docker! This message shows that your installation appears to be working correctly.`

---

### Pasos — Parte 2: App Node.js

1. **Crea la estructura del proyecto**
   ```bash
   mkdir mi-app-docker
   cd mi-app-docker
   mkdir src
   ```

2. **Inicializa el proyecto Node.js**
   ```bash
   npm init -y
   ```

3. **Copia el contenido de los archivos**

   Crea el archivo `src/index.js` y el `Dockerfile` en la raíz del proyecto copiando el contenido de la
   sección **Contenido de los Archivos** de este README.

4. **Instala las dependencias**
   ```bash
   npm install
   ```

5. **Construye la imagen Docker**
   ```bash
   docker build -t mi-app .
   ```
   *Salida esperada:* Docker procesará cada instrucción del `Dockerfile` como una capa y mostrará `Successfully built` al finalizar.

6. **Crea y ejecuta el contenedor**
   ```bash
   docker run --name contenedor-node -p 7000:7000 mi-app
   ```

7. **Verifica el funcionamiento**

   Abre el navegador y accede a `http://localhost:7000`. La respuesta esperada es:
   ```
   ¡Hola desde Node.js en Docker!
   ```

---

## Conceptos Clave de Docker

| Concepto            | Descripción                                                                                                                      |
|---------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `Imagen`            | Plantilla inmutable y de solo lectura que contiene el sistema de archivos y la configuración necesaria para crear contenedores.  |
| `Contenedor`        | Instancia en ejecución de una imagen. Es aislado, efímero y comparte el kernel del sistema operativo host.                       |
| `Docker Hub`        | Registro público oficial de imágenes Docker, equivalente a un "npm" pero para contenedores.                                      |
| `Dockerfile`        | Archivo de texto con instrucciones declarativas que definen, capa a capa, cómo construir una imagen personalizada.               |
| `docker build`      | Lee el `Dockerfile` y construye una nueva imagen local, identificándola con un tag (`-t nombre`).                                |
| `docker run`        | Combina `docker create` y `docker start` en un solo paso: crea el contenedor a partir de una imagen y lo ejecuta inmediatamente. |
| `Port Binding (-p)` | Mapea un puerto del host a un puerto del contenedor con el formato `-p <host>:<contenedor>`, exponiendo el servicio al exterior. |
| `node:alpine`       | Imagen base oficial de Node.js sobre Alpine Linux. Minimiza el tamaño de la imagen final al usar una distribución ultra-ligera.  |
| `docker ps -a`      | Lista todos los contenedores existentes independientemente de su estado (en ejecución, detenido, creado).                        |
| `WORKDIR`           | Instrucción del `Dockerfile` que establece el directorio de trabajo dentro del contenedor para los comandos subsiguientes.       |