# MapReduce en Python: Accesos Fuera de Horario Laboral || `CLASE 15 (08|07|2026)`

Se hizo este proyecto como un ejemplo introductorio práctico para implementar un flujo de **MapReduce en Python**, aplicado al análisis
de un archivo de **logs** de acceso de usuarios. El objetivo principal es dividir el archivo `logs` en fragmentos (*splits*),
procesarlos en paralelo con una fase de **map** que detecta los registros fuera del horario laboral (antes de las `08:00:00` o después
de las `18:00:00`) y emite pares `usuario\t1`, para luego consolidarlos en una fase de **reduce** que agrega los resultados en una tabla
final (`resultado.md`) con la cantidad de veces que cada usuario llegó fuera de su horario laboral. Todo el flujo puede ejecutarse dentro
de contenedores **Docker**, orquestados con **Docker Compose**.

## Vista General del Proyecto

El archivo de entrada `logs` contiene registros con el formato `fecha hora usuario:nombre` (por ejemplo, `2025-07-20 07:15:02
usuario:ana`). Primero, `split_entrada.sh` divide `logs` en varios archivos más pequeños dentro de la carpeta `splits/` (`part_00.txt`,
`part_01.txt`, `part_02.txt`, `part_03.txt`, ...). Luego, `mapper.py` se ejecuta sobre cada fragmento: extrae la hora y el usuario de
cada línea y, si la hora está fuera del rango `08:00:00`–`18:00:00` (horario laboral), escribe un par `usuario\t1` en el archivo `.out`
correspondiente. Finalmente, `reducer.py` recorre todos los archivos `.out` de `splits/`, suma las apariciones de cada usuario usando un
`Counter`, y escribe el resultado consolidado y ordenado alfabéticamente en `resultado.md` como una tabla Markdown con el número de
accesos fuera de horario por usuario.

---

### Infraestructura Docker

| Recurso                     | Responsabilidad                                                                                                                            |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `Contenedor(es) Map/Reduce` | Ejecutan los scripts de Python (`mapper.py` / `reducer.py`) sobre los archivos de la carpeta `splits/` sin depender de servicios externos. |

> **Nota:** no hay una base de datos involucrada: todo el estado se maneja mediante archivos de texto en disco (`logs`, `splits/*.txt`,
> `splits/*.txt.out`, `resultado.md`).

### Módulos del Sistema (Archivos de Configuración)

| Archivo / Componente | Responsabilidad                                                                                                                                                        |
|----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `docker-compose.yml` | Archivo declarativo que define y orquesta el/los servicio(s) necesarios para ejecutar el pipeline de MapReduce dentro de contenedores.                                 |
| `Dockerfile`         | Define la imagen base de Python usada para ejecutar `split_entrada.sh`, `mapper.py` y `reducer.py` dentro del contenedor.                                              |
| `split_entrada.sh`   | Script de shell que divide `logs` en múltiples fragmentos (`part_00.txt`, `part_01.txt`, `part_02.txt`, `part_03.txt`, ...) dentro de `splits/`.                       |
| `logs`               | Archivo de entrada con los registros de acceso: fecha, hora y usuario (`fecha hora usuario:nombre`).                                                                   |
| `mapper.py`          | Fase de **Map**: lee un fragmento de logs, extrae hora y usuario, filtra los registros fuera del horario laboral (`< 08:00:00` o `> 18:00:00`) y escribe `usuario\t1`. |
| `reducer.py`         | Fase de **Reduce**: recorre todos los `.out` de `splits/`, agrega los conteos por usuario con `Counter` y genera la tabla final en `resultado.md`.                     |
| `splits/`            | Carpeta con los fragmentos de logs (`part_XX.txt`) y sus respectivas salidas del mapper (`part_XX.txt.out`).                                                           |
| `resultado.md`       | Tabla Markdown con el resultado final: cada usuario y su cantidad total de accesos fuera de horario, ordenados alfabéticamente.                                        |

---

## Fases del Proceso (equivalente a "Endpoints Disponibles")

| Fase     | Script             | Descripción                                                                                                            |
|----------|--------------------|------------------------------------------------------------------------------------------------------------------------|
| `Split`  | `split_entrada.sh` | Divide el archivo `logs` en fragmentos más pequeños dentro de `splits/`.                                               |
| `Map`    | `mapper.py`        | Procesa un fragmento, identifica los registros fuera del horario laboral (`08:00:00`–`18:00:00`) y emite `usuario\t1`. |
| `Reduce` | `reducer.py`       | Combina todos los `.out` de `splits/`, suma los accesos fuera de horario por usuario y genera `resultado.md`.          |

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta                                                       | Versión / Enlace                                 |
|-------------------------------------------------------------------|--------------------------------------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Última versión estable (incluye Docker Compose). |
| [Git](https://git-scm.com/)                                       | Para clonar el repositorio localmente.           |
| [Python](https://www.python.org/downloads/)                       | 3.x (solo si se ejecuta fuera de Docker).        |

### Pasos de Ejecución

1. **Clonar el Repositorio**
   Abre tu terminal y descarga el código fuente a tu máquina local ejecutando:
    ```bash
    git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
    cd src/Clase_15/Logs
    ```
2. **Levantar la Infraestructura con Docker Compose**
   Ejecuta el siguiente comando en la misma ruta donde se encuentra el archivo `docker-compose.yml`:
    ```bash
    docker-compose up -d
    ```
   *El flag `-d` ejecuta los contenedores en segundo plano (detached mode).*
3. **Verificar el Resultado del MapReduce**
   Una vez que el pipeline termine de ejecutarse (split → map → reduce), revisa el archivo `resultado.md` generado en la raíz del
   proyecto; deberías ver una tabla con cada `usuario` y el número de veces que registró un acceso fuera del horario laboral
   (`08:00:00`–`18:00:00`).
4. **Detener los Servicios (Opcional)**
   Para apagar y eliminar los contenedores generados, ejecuta:
   ```bash
   docker-compose down
   ```

---

## Características Implementadas

* **MapReduce simplificado en Python:** implementación manual del patrón Map → Reduce sin frameworks externos (Hadoop, Spark, etc.).
* **División de datos con Shell Script:** `split_entrada.sh` fragmenta el archivo `logs` para simular el procesamiento distribuido.
* **Filtrado por horario laboral:** `mapper.py` detecta accesos anteriores a `08:00:00` o posteriores a `18:00:00` a partir de la hora
  registrada en cada línea del log.
* **Fase Reduce con `Counter`:** agregación eficiente de resultados por usuario usando la librería estándar `collections.Counter`.
* **Contenerización con Docker:** el pipeline completo puede ejecutarse dentro de contenedores, orquestado con Docker Compose.
* **Salida en formato Markdown:** el resultado final (`resultado.md`) es una tabla lista para visualizar directamente en GitHub o
  cualquier visor Markdown.

---

## Conceptos Clave

| Concepto         | Descripción                                                                                                                    |
|------------------|--------------------------------------------------------------------------------------------------------------------------------|
| `MapReduce`      | Patrón de procesamiento de datos en dos fases (Map y Reduce) originalmente pensado para cómputo distribuido a gran escala.     |
| `Map`            | Fase que transforma cada línea de log en pares clave-valor (aquí, `usuario\t1` cuando el acceso está fuera de horario).        |
| `Reduce`         | Fase que agrupa y agrega los pares clave-valor generados por el Map (aquí, sumando los accesos fuera de horario por usuario).  |
| `Docker Compose` | Herramienta que permite definir y ejecutar aplicaciones Docker de uno o más contenedores mediante un archivo YAML declarativo. |
| `Dockerfile`     | Archivo de instrucciones que define cómo construir la imagen del contenedor donde corren los scripts de Python.                |
| `Counter`        | Clase de la librería estándar de Python (`collections`) usada para contar elementos de forma eficiente en la fase de Reduce.   |

---