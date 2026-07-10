# MapReduce en Python: Conteo de Palabras con Docker || `CLASE 15 (08|07|2026)`

Se hizo este proyecto como un ejemplo introductorio práctico para implementar un flujo de **MapReduce en Python**, aplicado al conteo de
palabras de un texto de entrada. El objetivo principal es dividir un archivo (`entrada.txt`) en fragmentos (*splits*), procesarlos en
paralelo con una fase de **map** que emite pares `palabra\tconteo`, y luego consolidarlos en una fase de **reduce** que agrega los
resultados en una tabla final (`resultado.md`). Todo el flujo puede ejecutarse dentro de contenedores **Docker**, orquestados con
**Docker Compose**.

## Vista General del Proyecto

El proyecto sigue el patrón clásico de MapReduce: primero, `split_entrada.sh` divide `entrada.txt` en varios archivos de texto más
pequeños dentro de la carpeta `splits/` (`part_00.txt`, `part_01.txt`, `part_02.txt`, ...). Luego, `mapper.py` se ejecuta sobre cada
fragmento, tokenizando el texto en palabras y generando un archivo `.out` por cada split con pares `palabra\t1`. Finalmente,
`reducer.py` recorre todos los archivos `.out` de `splits/`, suma las ocurrencias de cada palabra usando un `Counter`, y escribe el
resultado consolidado y ordenado alfabéticamente en `resultado.md` como una tabla Markdown.

---

### Infraestructura Docker

| Recurso                     | Responsabilidad                                                                                                                            |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `Contenedor(es) Map/Reduce` | Ejecutan los scripts de Python (`mapper.py` / `reducer.py`) sobre los archivos de la carpeta `splits/` sin depender de servicios externos. |

> **Nota:** a diferencia del proyecto de la API REST, aquí no hay una base de datos: todo el estado se maneja mediante archivos de texto
> en disco (`entrada.txt`, `splits/*.txt`, `splits/*.txt.out`, `resultado.md`).

### Módulos del Sistema (Archivos de Configuración)

| Archivo / Componente | Responsabilidad                                                                                                                          |
|----------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| `docker-compose.yml` | Archivo declarativo que define y orquesta el/los servicio(s) necesarios para ejecutar el pipeline de MapReduce dentro de contenedores.   |
| `Dockerfile`         | Define la imagen base de Python usada para ejecutar `split_entrada.sh`, `mapper.py` y `reducer.py` dentro del contenedor.                |
| `split_entrada.sh`   | Script de shell que divide `entrada.txt` en múltiples fragmentos (`part_00.txt`, `part_01.txt`, `part_02.txt`, ...) dentro de `splits/`. |
| `entrada.txt`        | Texto de entrada (~1000 palabras) que sirve como fuente de datos para el conteo de palabras.                                             |
| `mapper.py`          | Fase de **Map**: lee un fragmento de texto, tokeniza cada palabra en minúsculas y escribe pares `palabra\t1` en un archivo `.out`.       |
| `reducer.py`         | Fase de **Reduce**: recorre todos los `.out` de `splits/`, agrega los conteos con `Counter` y genera la tabla final en `resultado.md`.   |
| `splits/`            | Carpeta con los fragmentos de entrada (`part_XX.txt`) y sus respectivas salidas del mapper (`part_XX.txt.out`).                          |
| `resultado.md`       | Tabla Markdown con el resultado final: cada palabra única y su conteo total, ordenados alfabéticamente.                                  |

---

## Fases del Proceso (equivalente a "Endpoints Disponibles")

| Fase     | Script             | Descripción                                                                                  |
|----------|--------------------|----------------------------------------------------------------------------------------------|
| `Split`  | `split_entrada.sh` | Divide `entrada.txt` en fragmentos más pequeños dentro de `splits/`.                         |
| `Map`    | `mapper.py`        | Procesa un fragmento, tokeniza el texto y emite pares `palabra\t1` en un archivo `.out`.     |
| `Reduce` | `reducer.py`       | Combina todos los `.out` de `splits/`, suma los conteos por palabra y genera `resultado.md`. |

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta                                                       | Versión / Enlace                                 |
|-------------------------------------------------------------------|--------------------------------------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Última versión estable (incluye Docker Compose). |
| [Git](https://git-scm.com/)                                       | Para clonar el repositorio localmente.           |
| [Python](https://www.python.org/downloads/)                       | 3.x (solo si se ejecuta fuera de Docker).        |

## Características Implementadas

* **MapReduce simplificado en Python:** implementación manual del patrón Map → Reduce sin frameworks externos (Hadoop, Spark, etc.).
* **División de datos con Shell Script:** `split_entrada.sh` fragmenta la entrada para simular el procesamiento distribuido.
* **Fase Map independiente por fragmento:** `mapper.py` puede ejecutarse de forma aislada sobre cualquier archivo de `splits/`.
* **Fase Reduce con `Counter`:** agregación eficiente de resultados usando la librería estándar `collections.Counter`.
* **Contenerización con Docker:** el pipeline completo puede ejecutarse dentro de contenedores, orquestado con Docker Compose.
* **Salida en formato Markdown:** el resultado final (`resultado.md`) es una tabla lista para visualizar directamente en GitHub o cualquier
  visor Markdown.

---

## Conceptos Clave

| Concepto         | Descripción                                                                                                                    |
|------------------|--------------------------------------------------------------------------------------------------------------------------------|
| `MapReduce`      | Patrón de procesamiento de datos en dos fases (Map y Reduce) originalmente pensado para cómputo distribuido a gran escala.     |
| `Map`            | Fase que transforma cada elemento de entrada en pares clave-valor (aquí, `palabra\t1`).                                        |
| `Reduce`         | Fase que agrupa y agrega los pares clave-valor generados por el Map (aquí, sumando los conteos por palabra).                   |
| `Docker Compose` | Herramienta que permite definir y ejecutar aplicaciones Docker de uno o más contenedores mediante un archivo YAML declarativo. |
| `Dockerfile`     | Archivo de instrucciones que define cómo construir la imagen del contenedor donde corren los scripts de Python.                |
| `Counter`        | Clase de la librería estándar de Python (`collections`) usada para contar elementos de forma eficiente en la fase de Reduce.   |

---