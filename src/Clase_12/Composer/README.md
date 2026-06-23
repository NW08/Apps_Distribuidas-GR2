# Balanceador de Carga: Nginx + Apache HTTPD || `CLASE 12 (17|06|2026)`

Se hizo este proyecto como un ejemplo introductorio práctico para implementar una arquitectura de balanceo de carga en páginas web mediante
**Docker**. El objetivo principal es desplegar un proxy inverso con **Nginx** que distribuya el tráfico HTTP entrante entre un conjunto de
tres servidores web backend independientes. Cada servidor web expone una página estática en HTML puro y está contenido en una imagen
ultraligera **httpd:alpine**, garantizando un consumo mínimo de recursos.

## Vista General del Proyecto

El proyecto despliega un entorno de red donde un único punto de entrada (el contenedor de Nginx) gestiona las peticiones de los clientes y
las reparte de manera eficiente entre tres contenedores web (`alpha`, `beta` y `omega`). Esta configuración es fundamental para garantizar
alta disponibilidad, ya que evita que un solo servidor se sobrecargue de peticiones. A diferencia de implementaciones manuales anteriores,
toda esta infraestructura de cuatro contenedores se orquesta de manera centralizada y automatizada utilizando **Docker Compose**.

---

### Infraestructura Docker / Red

| Recurso                | Responsabilidad                                                                                                                                                                   |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Contenedor Nginx`     | Actúa como proxy inverso y balanceador de carga. Recibe todas las peticiones en el puerto principal y las redirige hacia los nodos internos según su algoritmo (ej. Round-Robin). |
| `Contenedor Web Alpha` | Nodo de procesamiento 1. Basado en `httpd:alpine`, encargado de servir el contenido estático alojado en la carpeta `alpha/`.                                                      |
| `Contenedor Web Beta`  | Nodo de procesamiento 2. Basado en `httpd:alpine`, encargado de servir el contenido estático alojado en la carpeta `beta/`.                                                       |
| `Contenedor Web Omega` | Nodo de procesamiento 3. Basado en `httpd:alpine`, encargado de servir el contenido estático alojado en la carpeta `omega/`.                                                      |

### Módulos del Sistema (Archivos de Configuración)

| Archivo / Componente | Responsabilidad                                                                                                                                                                   |
|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `docker-compose.yml` | Archivo declarativo que define y orquesta todos los contenedores, redes internas y mapeo de volúmenes necesarios para levantar el ecosistema completo con un solo comando.        |
| `nginx.conf`         | Archivo de configuración principal del servidor Nginx. Aquí se define el bloque `upstream` que agrupa a los tres servidores web y las reglas de redirección del tráfico.          |
| `index.html` (x3)    | Archivos HTML ubicados dentro de cada subcarpeta (`alpha`, `beta`, `omega`). Poseen contenido diferenciado para poder comprobar visualmente qué servidor respondió a la petición. |

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta                                                       | Versión / Enlace                                |
|-------------------------------------------------------------------|-------------------------------------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Última versión estable (incluye Docker Compose) |
| [Git](https://git-scm.com/)                                       | Para clonar el repositorio localmente.          |

### Pasos de Ejecución

1. **Clonar el Repositorio**
   Abre tu terminal y descarga el código fuente a tu máquina local ejecutando:

    ```bash
    git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
    cd src/Clase_12/Composer
    ```

2. **Levantar la Infraestructura con Docker Compose**
   A diferencia de la creación manual de contenedores, aquí utilizaremos el orquestador. Ejecuta el siguiente comando en la misma ruta donde
   se encuentra el archivo `docker-compose.yml`:

    ```bash
    docker-compose up -d
    ```
   *El flag `-d` ejecuta los contenedores en segundo plano (detached mode).*

3. **Verificar el Balanceo de Carga**
   Abre tu navegador web y dirígete a `http://localhost` (o el puerto configurado). Refresca la página (`F5` o `Ctrl + R`) varias veces;
   deberías observar cómo el contenido del `index.html` alterna entre las versiones de `alpha`, `beta` y `omega`, demostrando que Nginx está
   distribuyendo el tráfico correctamente.

4. **Detener los Servicios (Opcional)**
   Para apagar y eliminar los contenedores generados, ejecuta:
   ```bash
   docker-compose down
   ```

---

## Características Implementadas

* **Balanceo de Carga Básico:** Distribución equitativa de peticiones web entre múltiples contenedores para evitar cuellos de botella y
  maximizar la concurrencia.
* **Orquestación con Docker Compose:** Migración del despliegue manual a una infraestructura como código (IaC), permitiendo levantar 4
  servicios simultáneamente con sus respectivas redes en un solo paso.
* **Optimización de Recursos (Alpine):** Uso de la versión `alpine` del servidor web Apache (`httpd`), la cual reduce drásticamente el
  tamaño de la imagen y la superficie de ataque del contenedor.
* **Inyección de Configuraciones:** Mapeo de archivos de configuración desde el entorno host (la máquina local) hacia los contenedores
  mediante volúmenes, permitiendo personalizar `nginx.conf` sin tener que reconstruir la imagen.

---

## Conceptos Clave

| Concepto               | Descripción                                                                                                                                        |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `Balanceador de Carga` | Dispositivo o servicio que distribuye de forma metódica el tráfico de red o de aplicaciones a través de un grupo de servidores (pool).             |
| `Nginx`                | Software de código abierto de alto rendimiento utilizado como servidor web, proxy inverso, balanceador de carga y caché HTTP.                      |
| `Docker Compose`       | Herramienta que permite definir y ejecutar aplicaciones Docker de múltiples contenedores basándose en las reglas de un archivo YAML.               |
| `httpd:alpine`         | Imagen de Docker del servidor web Apache (HTTP Daemon) construida sobre Alpine Linux, conocida por ser minimalista, rápida y segura.               |
| `Proxy Inverso`        | Servidor que se sitúa frente a los servidores backend y actúa como intermediario para las peticiones de los clientes, ocultando la topología real. |