# Cine Dashboard: Node.js + MySQL || `CLASE 10 (03|06|2026)`

Se hizo este proyecto como un ejercicio práctico para consumir una base de datos **MySQL** contenerizada mediante una aplicación **Node.js +
Express**. Se desarrolló un dashboard de estilo oscuro (inspirado en Netflix) que permite realizar operaciones CRUD sobre una tabla de
películas directamente desde el navegador, asegurando la correcta codificación de caracteres y la accesibilidad web.

## Vista General del Proyecto

El proyecto se divide en el despliegue de una base de datos poblada inicialmente a través de volúmenes mapeados, y la creación de un
servidor backend que expone una API RESTful. El frontend es una Single Page Application (SPA) en HTML, CSS y JS nativo que interactúa con la
API. Todo se estructuró dando prioridad a buenas prácticas básicas como el uso correcto de soporte UTF-8 completo en la base de datos y
etiquetas de accesibilidad en los formularios.

---

### Infraestructura Docker / Base de Datos

| Recurso            | Responsabilidad                                                                                                 |
|--------------------|-----------------------------------------------------------------------------------------------------------------|
| `Contenedor MySQL` | Motor de base de datos ejecutándose en Docker, al que la app Node se conecta localmente (o por red Docker).     |
| `Volumen (bdd)`    | Directorio mapeado al contenedor para garantizar la ejecución de scripts iniciales o persistencia de datos.     |
| `peliculas.sql`    | Script de inicialización. Crea la base de datos `cinema` con codificación `utf8mb4` y puebla la tabla `movies`. |

### Módulo `web` (App Node.js)

| Archivo             | Responsabilidad                                                                                                              |
|---------------------|------------------------------------------------------------------------------------------------------------------------------|
| `index.js`          | Servidor Express. Expone los endpoints REST (`GET`, `POST`, `PUT`, `DELETE`) y sirve el frontend estático.                   |
| `public/index.html` | Interfaz gráfica interactiva. Contiene estilos CSS integrados y lógica JS (Fetch API) para inyectar datos y manejar el CRUD. |
| `package.json`      | Manifiesto del proyecto. Declara las dependencias mínimas necesarias: `express` y `mysql2`.                                  |

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta                                                       | Versión / Enlace                                        |
|-------------------------------------------------------------------|---------------------------------------------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Última versión estable (para el contenedor de MySQL)    |
| [Node.js](https://nodejs.org/)                                    | 18 LTS o superior (para ejecutar el backend localmente) |

> **Nota:** Docker es necesario para levantar el motor de base de datos de forma aislada. Node.js se utiliza para ejecutar el servidor
> backend de manera local e interactuar con dicho contenedor.

### Pasos de Ejecución

1. **Preparar la Base de Datos**
   Asegúrate de tener un contenedor de MySQL en ejecución y de haber mapeado la carpeta `bdd/` (que contiene `peliculas.sql`) al directorio
   de inicialización del contenedor, o ejecuta el script manualmente dentro de tu base de datos para crear la tabla `movies` y los registros
   iniciales.

2. **Instalar Dependencias del Servidor**
   Abre una terminal en la raíz de la carpeta del proyecto (donde se encuentra `package.json`) y ejecuta:

    ```bash
    npm install
    ```

3. **Iniciar la Aplicación Node.js**
   Levanta el servidor Express ejecutando:

    ```bash
    node index.js
    ```

   *Salida esperada:* `Servidor corriendo en http://localhost:7000`

4. **Acceder al Dashboard**
   Abre tu navegador web y dirígete a `http://localhost:7000`. Deberías ver la interfaz oscura cargando directamente las películas desde tu
   contenedor MySQL.

---

## Características Implementadas

* **Codificación Universal:** Uso de `utf8mb4` en MariaDB/MySQL para soportar el espectro completo de caracteres Unicode sin truncamientos.
* **API REST con Express:** Endpoints dedicados para interactuar asíncronamente desde el cliente sin necesidad de recargar la página.
* **Renderizado Dinámico del DOM:** Uso de la API `fetch` en el frontend para consumir los datos e inyectar tarjetas HTML (`.movie-card`) en
  tiempo de ejecución.
* **Accesibilidad Web (a11y):** Implementación correcta de atributos `for` en etiquetas `<label>` vinculadas a los `id` de los `<input>`,
  mejorando soporte para lectores de pantalla y clics.
* **Gestión de Falsos Positivos del IDE:** Comprensión del análisis estático de herramientas como IntelliJ/WebStorm al inyectar clases CSS
  dinámicamente mediante JavaScript.

---

## Conceptos Clave

| Concepto               | Descripción                                                                                                                              |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| `utf8mb4`              | Implementación completa de UTF-8 en MySQL que permite almacenar cualquier carácter (a diferencia del `utf8` clásico de MySQL).           |
| `Fetch API`            | Interfaz nativa de JavaScript moderno para realizar peticiones HTTP asíncronas hacia la API de Express.                                  |
| `a11y (Accesibilidad)` | Prácticas de diseño y desarrollo web, como el uso de `<label>`, que aseguran que la aplicación sea usable por personas con discapacidad. |
| `Análisis Estático`    | Revisión de código que hace el IDE sin ejecutarlo; puede causar advertencias de "clases no usadas" si el HTML se genera dinámicamente.   |
| `Pool de Conexiones`   | Método en `mysql2` que reutiliza conexiones a la base de datos, mejorando el rendimiento frente a crear una nueva conexión por consulta. |
