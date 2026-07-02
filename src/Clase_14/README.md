# API REST Farmacia: Spring Boot + MySQL + Docker || `CLASE 14 (01|07|2026)`

Se hizo este proyecto como un ejemplo introductorio práctico para implementar una **API REST con Spring Boot** que consume información desde
una base de datos **MySQL** contenerizada con **Docker**. El objetivo principal es exponer un conjunto de endpoints básicos que permitan
consultar el catálogo de productos de una farmacia (almacenado en la tabla `productos`), utilizando **Spring Data JPA** para el acceso a
datos. Posteriormente, la propia aplicación Spring Boot también se conteneriza, quedando todo el ecosistema (API + BDD) orquestado con
**Docker Compose**.

## Vista General del Proyecto

El proyecto despliega un entorno de dos contenedores: uno para la base de datos **MySQL** (inicializada automáticamente con el script
`farmacia.sql`) y otro para la aplicación **Spring Boot**, que se comunica con la BDD a través de una red interna de Docker. La API expone
endpoints para listar, buscar por ID, filtrar por categoría y buscar por nombre comercial los productos del catálogo. Las credenciales de
conexión se gestionan mediante variables de entorno (`.env`), evitando exponer datos sensibles directamente en el código o en
`application.properties`.

---

### Infraestructura Docker / Red

| Recurso                        | Responsabilidad                                                                                                                                   |
|--------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `Contenedor App (Spring Boot)` | Ejecuta la API REST empaquetada como `.jar`. Expone el puerto `8080` y se conecta a la base de datos usando las credenciales definidas en `.env`. |
| `Contenedor DB (MySQL)`        | Almacena y sirve los datos del catálogo de productos. Se inicializa automáticamente con el script `farmacia.sql` mediante un volumen de arranque. |

### Módulos del Sistema (Archivos de Configuración)

| Archivo / Componente          | Responsabilidad                                                                                                                                                                   |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `docker-compose.yml`          | Archivo declarativo que define y orquesta los servicios `app` y `db`, la red interna y el mapeo de volúmenes necesarios para levantar el ecosistema completo con un solo comando. |
| `Dockerfile`                  | Define el build multietapa de la aplicación: compila el proyecto con Maven y genera una imagen ligera basada en JRE para ejecutar el `.jar` resultante.                           |
| `.env`                        | Contiene las variables de entorno sensibles (usuario, contraseña y nombre de la base de datos) inyectadas tanto en el contenedor de MySQL como en la app.                         |
| `application.properties`      | Configuración de Spring Boot: URL de conexión JDBC, credenciales de la BDD y propiedades de Hibernate/JPA.                                                                        |
| `pom.xml`                     | Archivo de Maven que define las dependencias del proyecto (Spring Web, Spring Data JPA, driver de MySQL, Lombok) y el plugin de empaquetado.                                      |
| `resources/db/farmacia.sql`   | Script SQL que crea la tabla `productos` y la puebla con datos iniciales; se monta como script de inicialización del contenedor MySQL.                                            |
| `src/DemoApplication.java`    | Clase principal que arranca el contexto de Spring Boot (`@SpringBootApplication`).                                                                                                |
| `src/Controller.java`         | Controlador REST (`@RestController`) que expone los endpoints HTTP y delega la lógica de acceso a datos al repositorio.                                                           |
| `src/Producto.java`           | Entidad JPA (`@Entity`) mapeada a la tabla `productos`, con sus columnas correspondientes.                                                                                        |
| `src/ProductoRepository.java` | Interfaz `JpaRepository` que provee las operaciones CRUD y consultas derivadas (por categoría y nombre comercial).                                                                |

---

## Endpoints Disponibles

| Método | Ruta                                | Descripción                                                                         |
|--------|-------------------------------------|-------------------------------------------------------------------------------------|
| `GET`  | `/`                                 | Endpoint de introducción; confirma que la API está corriendo y conectada a MySQL.   |
| `GET`  | `/productos`                        | Devuelve el listado completo de productos.                                          |
| `GET`  | `/productos/{id}`                   | Devuelve un producto específico según su `id`.                                      |
| `GET`  | `/productos/categoria/{categoria}`  | Filtra productos por categoría exacta.                                              |
| `GET`  | `/productos/buscar?nombre={nombre}` | Busca productos cuyo nombre comercial coincida (parcial) con el parámetro `nombre`. |

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta                                                                                           | Versión / Enlace                                 |
|-------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/)                                     | Última versión estable (incluye Docker Compose). |
| [Git](https://git-scm.com/)                                                                           | Para clonar el repositorio localmente.           |
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 26                                               |

## Características Implementadas

* **API REST con Spring Boot:** Exposición de endpoints HTTP para consultar el catálogo de productos usando `@RestController`.
* **Persistencia con Spring Data JPA:** Acceso a datos mediante `JpaRepository`, incluyendo consultas derivadas (`findByCategoria`,
  `findByNombreComercial`) sin necesidad de escribir SQL manual.
* **Contenerización Completa:** Tanto la base de datos MySQL como la propia aplicación Spring Boot se ejecutan como contenedores Docker,
  orquestados con Docker Compose.
* **Inicialización Automática de la BDD:** El script `farmacia.sql` se ejecuta automáticamente al levantar el contenedor de MySQL,
  garantizando datos de prueba disponibles desde el primer arranque.
* **Gestión Segura de Credenciales:** Uso de un archivo `.env` para inyectar variables sensibles (usuario, contraseña, nombre de BDD) sin
  exponerlas en el código fuente.

---

## Conceptos Clave

| Concepto          | Descripción                                                                                                                                 |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| `Spring Boot`     | Framework de Java que simplifica la creación de aplicaciones y APIs REST mediante autoconfiguración y un servidor embebido.                 |
| `Spring Data JPA` | Módulo de Spring que abstrae el acceso a datos, permitiendo definir repositorios e infiriendo consultas a partir del nombre de los métodos. |
| `MySQL`           | Sistema de gestión de bases de datos relacional de código abierto, utilizado aquí para almacenar el catálogo de productos.                  |
| `Docker Compose`  | Herramienta que permite definir y ejecutar aplicaciones Docker de múltiples contenedores basándose en las reglas de un archivo YAML.        |
| `Dockerfile`      | Archivo de instrucciones que define cómo construir la imagen de la aplicación (build multietapa: compilación con Maven + imagen JRE).       |
| `.env`            | Archivo de variables de entorno usado para desacoplar la configuración sensible del código fuente y de los archivos versionados.            |

---
