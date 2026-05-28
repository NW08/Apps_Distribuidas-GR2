# Examen Bimestral #01 (Girón María & Ortiz Josué) || `CLASE 09 (27|05|2026)`

Para la presente prueba práctica se evaluarán los conocimientos básicos sobre **Docker**.

---

## Parte 1: Crear Contenedor de Base de Datos

* Descarga la imagen oficial de **MySQL** (`mysql:8`).
* Levanta un contenedor llamado `mysql-peliculas` con:

    * Usuario: `root`
    * Contraseña: `root`
* Expón el puerto `3306`.
* Finalmente, carga los datos del archivo `películas.sql`.

---

## Parte 2: Node.js con Docker

* Crea un proyecto Node.js utilizando:

```bash
npm init -y
```

* Instala `mysql2` o `sequelize`.
* Posteriormente, escribe un archivo `index.js` que:

    * Se conecta a la base de datos.
    * Muestra todas las películas en consola.

---

## Parte 3: CRUD

* Personaliza el archivo `index.js` para que permita:

    * Leer películas
    * Crear películas
    * Actualizar películas
    * Eliminar películas

> Puede usar una plantilla para mejorar la vista.

---

## Parte 4: DockerFile

* Genera el archivo `Dockerfile` para encapsular la aplicación en una imagen.
* Finalmente, despliega toda la aplicación distribuida.

---
