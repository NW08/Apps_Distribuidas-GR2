# Plataforma de Cursos: React + Node.js + MySQL Replicado || `CLASE 11 (10|06|2026)`

Se hizo este proyecto como un ejercicio práctico avanzado para implementar una arquitectura distribuida mediante **Docker**, conformada por
un total de 5 contenedores. El objetivo principal es desplegar un esquema de **Replicación MySQL (Maestro-Esclavos)** y consumirlo mediante
una aplicación Full-Stack contenerizada. El backend está desarrollado en **Node.js + Express**, y el frontend es una SPA construida con *
*React, Vite y Tailwind CSS**, enfocada en el rendimiento, la experiencia de usuario (UX) y el manejo de estado en tiempo real.

## Vista General del Proyecto

El proyecto despliega un ecosistema completo donde la base de datos se divide en un servidor maestro (para escritura) y dos esclavos (para
lectura), garantizando alta disponibilidad. El frontend interactúa dinámicamente con una API RESTful expuesta por el backend. Durante el
desarrollo, se priorizó la estandarización de nomenclaturas, corrección de consultas SQL y optimización de formularios. Ante limitaciones en
la estructura inicial de la base de datos, se implementaron soluciones creativas desde el cliente (usando el almacenamiento del navegador)
para mantener un flujo funcional e ininterrumpido sin comprometer la estabilidad del sistema.

---

### Infraestructura Docker / Base de Datos

| Recurso               | Responsabilidad                                                                                                                                   |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `Contenedor Master`   | Motor MySQL principal. Recibe todas las peticiones de escritura (como el registro de usuarios o actualizaciones de perfil) y propaga los cambios. |
| `Contenedor Slave_1`  | Réplica número 1 de la base de datos. Sincronizada con el maestro para distribuir las cargas de lectura (ej. listar cursos).                      |
| `Contenedor Slave_2`  | Réplica número 2 de la base de datos. Respaldo de lectura redundante.                                                                             |
| `Contenedor Backend`  | Servidor Node.js/Express que contiene toda la lógica de negocio, endpoints y controladores que interactúan con el pool de conexiones MySQL.       |
| `Contenedor Frontend` | Servidor que empaqueta y sirve la aplicación React/Vite.                                                                                          |

### Módulos del Sistema (Frontend y Backend)

| Archivo / Componente | Responsabilidad                                                                                                                                                                 |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AuthContext.jsx`    | Maneja el estado global de sesión. Incluye una función de actualización en tiempo real para que cambios (ej. editar nombre en `Perfil.jsx`) se reflejen sin recargar la página. |
| `Cursos y MisCursos` | Gestionan el catálogo y los cursos adquiridos. Al no existir la tabla relacional `usuarios_cursos` en la BD, simulan la compra guardando los IDs localmente en `localStorage`.  |
| `Vistas de Acceso`   | `Login.jsx`, `Registro.jsx` y `AdminPanel.jsx`. Utilizan enrutamiento inteligente (redirección de administradores) y validaciones HTML5 nativas para aliviar el servidor.       |
| `authController.js`  | Controlador backend que procesa la autenticación. Fue depurado para corregir una desestructuración fallida (`nombre` vs `name`) que guardaba usuarios en blanco.                |
| `cursoController.js` | Procesa las interacciones con cursos. Ante la ausencia de la tabla relacional, se modificó para simplemente incrementar el contador `usuarios_accedidos` en el curso objetivo.  |
| `Curso.js` (Modelo)  | Modelo de datos. Se corrigió un bug crítico en la consulta SQL donde se buscaba erróneamente por `usuario_id` en lugar de `id`.                                                 |

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta                                                       | Versión / Enlace                                         |
|-------------------------------------------------------------------|----------------------------------------------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Última versión estable (para orquestar los contenedores) |
| [Git](https://git-scm.com/)                                       | Para clonar el repositorio localmente.                   |

> **Nota:** Al estar toda la aplicación contenerizada (tanto front, back y bases de datos), necesitas crear las imágenes y posteriores
> contenedores para levantar el entorno completo. No es estrictamente necesario tener Node.js instalado en tu máquina host, a menos que
> desees ejecutar servicios por fuera del contenedor.

### Pasos de Ejecución

1. **Clonar el Repositorio**
   Abre tu terminal y descarga el código fuente a tu máquina local ejecutando:

    ```bash
    git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
    cd src/Clase_11
    ```

2. **Construir Imágenes y Levantar Contenedores Manualmente**
   Dado que no se utiliza Docker Compose, se deben compilar las imágenes personalizadas de la aplicación y posteriormente iniciar cada uno
   de los 5 contenedores de forma individual utilizando los comandos nativos de Docker.

3. **Verificar el Estado de la Replicación (Opcional)**
   Puedes revisar los logs del contenedor maestro y los esclavos para confirmar que la sincronización se estableció correctamente:

   ```bash
   docker logs nombre_contenedor_slave_uno
   ```

4. **Acceder a la Aplicación**
   Abre tu navegador web y dirígete a `http://localhost:80`. Podrás interactuar con la plataforma, registrarte y simular la compra de cursos
   inmediatamente.

---

## Características Implementadas

* **Arquitectura de Replicación BD:** Despliegue de un entorno robusto con un servidor maestro y dos esclavos sincronizados en tiempo real
  mediante binlogs.
* **Actualización Dinámica de Estado:** Uso de `Context` en React para propagar cambios de usuario de forma instantánea a través de toda la
  interfaz, eliminando recargas innecesarias.
* **Optimización de Validaciones:** Migración de validaciones estrictas del backend al frontend utilizando atributos nativos de HTML5 (
  `required`, `minLength`). Esto valida los datos antes del submit, ahorrando peticiones de red (y recursos) hacia el backend.
* **Enrutamiento Inteligente:** Lógica implementada en React Router para detectar roles y redirigir fluidamente al administrador a su
  respectivo `AdminPanel.jsx` tras el inicio de sesión.
* **Workarounds Funcionales:** Ante la ausencia física de tablas intermedias (`usuarios_cursos`), se estructuró una solución híbrida: el
  frontend retiene los cursos del usuario mediante `localStorage`, y el backend mantiene métricas incrementando un campo de
  `usuarios_accedidos`.
* **Código Limpio y Predecible:** Unificación global de nombres de variables y funciones entre controladores, modelos y componentes para
  asegurar la mantenibilidad a largo plazo.

---

## Conceptos Clave

| Concepto                 | Descripción                                                                                                                                       |
|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `Replicación MySQL`      | Técnica que permite copiar y mantener la información de una base de datos de un servidor principal (Maestro) a otros secundarios (Esclavos).      |
| `Validaciones HTML5`     | Uso nativo de validadores integrados en el navegador web que mejoran el rendimiento evitando procesamientos redundantes en el servidor Node.js.   |
| `Desestructuración (JS)` | Sintaxis que permite extraer valores de objetos. Su mala implementación (ej. mezclar español/inglés: nombre/name) puede causar pérdida de datos.  |
| `Context API`            | Herramienta nativa de React para compartir información (como el usuario autenticado) a cualquier nivel del árbol de componentes.                  |
| `LocalStorage`           | API de almacenamiento web que permite guardar pares clave-valor localmente en el navegador. Aquí utilizado temporalmente para simular relaciones. |