# Proyecto Multi-Módulo Maven con Lombok || `CLASE 06 (06|05|2026)`

Se hizo este proyecto como práctica introduciendo **Apache Maven** como herramienta de gestión y construcción de proyectos, y aplicando el
concepto de **artefactos inter-modulares** donde un módulo consume como dependencia a otro previamente compilado e instalado en el
repositorio local.

## Vista General del Proyecto

Este es un proyecto que demuestra cómo dividir responsabilidades en módulos Maven independientes: un módulo `Entidades` que define el
modelo de datos con **Lombok**, y un módulo `Servicio` que consume ese artefacto para implementar la lógica de negocio. Construido con
**Java 26** y gestionado íntegramente con **Apache Maven**. La comunicación entre módulos se logra mediante la referencia del `groupId` y
`artifactId` en el `pom.xml` del módulo dependiente, sin necesidad de copiar código fuente.

---

### Módulo `Entidades`

| Clase / Archivo | Responsabilidad                                                                                        |
|-----------------|--------------------------------------------------------------------------------------------------------|
| `Persona`       | Entidad/Modelo de datos. Usa `@Data` y `@AllArgsConstructor` de Lombok para evitar código boilerplate. |
| `App`           | Clase de prueba local que instancia una `Persona` y muestra sus atributos por consola.                 |
| `pom.xml`       | Define el artefacto (`Entidades`) y declara la dependencia de Lombok como procesador de anotaciones.   |

### Módulo `Servicio`

| Clase / Archivo   | Responsabilidad                                                                                                  |
|-------------------|------------------------------------------------------------------------------------------------------------------|
| `ServicioPersona` | Lógica de negocio. Implementa operaciones CRUD en memoria (insertar, mostrar, buscar, eliminar) sobre una lista. |
| `App`             | Clase de prueba que consume `ServicioPersona` y `Persona` del artefacto `Entidades` para validar el flujo.       |
| `pom.xml`         | Declara la dependencia hacia el artefacto `Entidades` por su `groupId` y `artifactId`.                           |

---

## Características Implementadas

- **Arquitectura Multi-Módulo con Maven:** El proyecto está dividido en dos módulos Maven independientes. Cada uno tiene su propio
  `pom.xml` y ciclo de vida de compilación, pero `Servicio` referencia a `Entidades` como si fuera cualquier otra dependencia de terceros.
- **Generación de Código con Lombok:** La clase `Persona` usa las anotaciones `@Data` (genera getters, setters, `equals`, `hashCode` y
  `toString`) y `@AllArgsConstructor` (genera el constructor con todos los campos), eliminando código repetitivo. Lombok está configurado
  como `annotationProcessorPath` en el plugin del compilador.
- **Instalación en Repositorio Local:** Para que `Servicio` pueda resolver el artefacto de `Entidades`, primero debe ejecutarse
  `mvn clean install` sobre `Entidades`. Esto empaqueta el módulo como `.jar` y lo deposita en el repositorio local de Maven
  (`~/.m2/repository`), haciéndolo disponible para cualquier otro proyecto.
- **Resolución de Dependencias por Coordenadas:** Maven identifica unívocamente un artefacto por su tripleta `groupId:artifactId:version`.
  El `pom.xml` de `Servicio` simplemente declara esas coordenadas y Maven se encarga de descargarlo o resolverlo desde el repositorio local
  automáticamente.
- **CRUD en Memoria:** `ServicioPersona` implementa las cuatro operaciones básicas sobre un `ArrayList<Persona>`: insertar, listar, buscar
  por cédula y eliminar por cédula usando un `Iterator` para evitar `ConcurrentModificationException`.

---

## Cómo Instalar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta / Librería                                                                                | Versión / Enlace                       |
|-------------------------------------------------------------------------------------------------------|----------------------------------------|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 26                                     |
| [Apache Maven](https://maven.apache.org/download.cgi)                                                 | 3.9.x o superior                       |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                      | Cualquier edición (Community/Ultimate) |

> **Nota:** Una vez descargado Apache Maven, descomprime el archivo y agrega la carpeta `bin` a la variable de
> entorno `PATH` de tu sistema (ej. en Windows: `MAVEN_HOME` apuntando a la carpeta raíz, y `%MAVEN_HOME%\bin`
> en el `Path`). Verifica la instalación con `mvn -version` en una terminal.

### Pasos

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
   cd src/Clase_06
   ```

2. **Crea los proyectos Maven en IntelliJ IDEA**

   Dado que los módulos viven en carpetas separadas, la forma recomendada es crearlos como proyectos Maven nuevos en
   IntelliJ y luego copiar el código fuente:
    - Ve a `File → New → Project...`
    - Selecciona **Maven Archetype** como tipo de proyecto.
    - Configura el `groupId`, `artifactId` y `version` según corresponda (ver los `pom.xml` del repositorio).
    - Copia las clases Java del repositorio dentro de `src/main/java/...` del proyecto recién creado.
    - Reemplaza el `pom.xml` generado con el del repositorio para asegurarte de tener las dependencias correctas.

3. **Compila e instala el módulo `Entidades` primero**

   Abre una terminal en la carpeta raíz del proyecto `Entidades` y ejecuta:
   ```bash
   mvn clean install
   ```
   Esto compilará el código, procesará las anotaciones de Lombok y depositará el `.jar` resultante en el
   repositorio local de Maven (`~/.m2/repository`). Deberías ver `BUILD SUCCESS` al finalizar.

4. **Compila el módulo `Servicio`**

   Con `Entidades` ya instalado, abre una terminal en la carpeta del proyecto `Servicio` y ejecuta:
   ```bash
   mvn clean install
   ```
   Maven resolverá automáticamente la dependencia de `Entidades` desde el repositorio local. Si `Entidades`
   no fue instalado primero, este paso fallará con un error de dependencia no encontrada.

5. **Ejecuta la aplicación**

    - Abre el módulo `Servicio` en IntelliJ IDEA.
    - Ejecuta la clase `App` del módulo `Servicio` directamente desde el IDE.

---

## Conceptos Clave de Maven

| Concepto                    | Descripción                                                                                                    |
|-----------------------------|----------------------------------------------------------------------------------------------------------------|
| `groupId`                   | Identificador de la organización o grupo (ej. `org.example`). Compartirlo entre módulos los relaciona.         |
| `artifactId`                | Nombre único del artefacto dentro del `groupId` (ej. `Entidades`, `Servicio`).                                 |
| `version`                   | Versión del artefacto (ej. `1.0-SNAPSHOT`). `-SNAPSHOT` indica que está en desarrollo activo.                  |
| `mvn clean install`         | Limpia compilaciones anteriores, compila, ejecuta tests y deposita el `.jar` en `~/.m2/repository`.            |
| Repositorio Local (`~/.m2`) | Caché local donde Maven almacena todas las dependencias descargadas e instaladas, incluyendo las propias.      |
| `scope: provided`           | La dependencia (ej. Lombok) es necesaria en compilación pero no se empaqueta en el `.jar` final del artefacto. |
| `annotationProcessorPaths`  | Indica a `maven-compiler-plugin` qué procesadores de anotaciones usar en tiempo de compilación (ej. Lombok).   |
