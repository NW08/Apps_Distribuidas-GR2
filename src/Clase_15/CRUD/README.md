# API REST Farmacia: Spring Boot + MySQL + Docker || `CLASE 15 (08|07|2026)`

Proyecto introductorio de una **API REST con Spring Boot** que consume información desde una base de datos **MySQL** contenerizada con *
*Docker**. Expone un CRUD completo sobre el catálogo de productos de una farmacia (tabla `productos`), usando **Spring Data JPA** para el
acceso a datos.

> **Cambio respecto a la versión anterior:** la API pasó de ser solo de lectura (GET) a un CRUD completo — se agregaron `POST`, `PUT` y
`DELETE`.

## Vista General del Proyecto

Dos contenedores: **MySQL** (inicializado con `farmacia.sql`) y **Spring Boot**, comunicados por una red interna de Docker. Las credenciales
se gestionan por variables de entorno (`.env`).

---

### Infraestructura Docker

| Recurso                        | Responsabilidad                                                                         |
|--------------------------------|-----------------------------------------------------------------------------------------|
| `Contenedor App (Spring Boot)` | Ejecuta la API en el puerto `8080`, se conecta a la BDD con las credenciales de `.env`. |
| `Contenedor DB (MySQL)`        | Almacena el catálogo de productos, inicializado con `farmacia.sql`.                     |

### Archivos del Proyecto

| Archivo / Componente          | Responsabilidad                                                                       |
|-------------------------------|---------------------------------------------------------------------------------------|
| `docker-compose.yml`          | Orquesta los servicios `app` y `db`.                                                  |
| `Dockerfile`                  | Build multietapa: compila con Maven, corre en imagen JRE.                             |
| `.env`                        | Variables sensibles (usuario, contraseña, nombre de BDD).                             |
| `application.properties`      | URL JDBC, credenciales y propiedades de Hibernate/JPA.                                |
| `resources/db/farmacia.sql`   | Crea y puebla la tabla `productos`.                                                   |
| `src/Controller.java`         | Expone los endpoints REST (CRUD completo).                                            |
| `src/Producto.java`           | Entidad JPA mapeada a `productos`.                                                    |
| `src/ProductoRepository.java` | `JpaRepository` con consultas derivadas (`findByCategoria`, `findByNombreComercial`). |

---

## Endpoints Disponibles

| Método   | Ruta                                | Descripción                            |
|----------|-------------------------------------|----------------------------------------|
| `GET`    | `/`                                 | Confirma que la API está corriendo.    |
| `GET`    | `/productos`                        | Lista completa de productos.           |
| `GET`    | `/productos/{id}`                   | Producto por `id` (404 si no existe).  |
| `GET`    | `/productos/categoria/{categoria}`  | Filtra por categoría exacta.           |
| `GET`    | `/productos/buscar?nombre={nombre}` | Busca por nombre comercial.            |
| `POST`   | `/productos`                        | Crea un producto nuevo.                |
| `PUT`    | `/productos/{id}`                   | Actualiza un producto existente.       |
| `DELETE` | `/productos/{id}`                   | Elimina un producto (204 si se borra). |

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

| Herramienta                                                                                           | Versión / Enlace                                 |
|-------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/)                                     | Última versión estable (incluye Docker Compose). |
| [Git](https://git-scm.com/)                                                                           | Para clonar el repositorio.                      |
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 26                                               |

## Características Implementadas

* **CRUD completo** sobre `productos`, vía `@RestController`.
* **Persistencia con Spring Data JPA**, incluyendo consultas derivadas sin SQL manual.
* **Contenerización completa** de app y BDD con Docker Compose.
* **Inicialización automática** de la BDD con `farmacia.sql`.
* **Gestión segura de credenciales** vía `.env`.