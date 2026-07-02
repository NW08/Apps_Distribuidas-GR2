# Prueba #02 (Girón María & Ortiz Josué) || `CLASE 13 (24|06|2026)`

Para la presente prueba práctica se implementará una infraestructura distribuida utilizando Docker, desplegando la aplicación web
proporcionada, junto con un balanceador de carga NGINX y bases de datos (maestro-esclavo).

1. INSTRUCCIONES GENERALES
    - Levantar la aplicación web proporcionada con Docker Compose.
    - Crear dos contenedores que ejecuten la aplicación.
    - Conectarlos correctamente a su base de datos respectiva.
    - Implementar el balanceador de carga NGINX.
    - El contenedor de NGINX debe estar configurado para distribuir peticiones entre app1 y app2.

2. APLICACIONES: App1 y App2
    - Las aplicaciones deben realizar un CRUD sobre la base de datos
        - Configurar dos nodos de base de datos (MySQL).
    - Configurar dos contenedores con MySQL
    - Realizar replicación,

> Los datos para realizar la presente evaluación se encuentran en el siguiente endpoint API REST: http://172.31.118.15:3000/selecciones