# Sistema Distribuido Java RMI - Directorio de Empleados || `CLASE 07 (13|05|2026)`

Se hizo este proyecto como implementación académica de un sistema distribuido Cliente-Servidor utilizando **Java RMI (Remote Method
Invocation)**. Demuestra la comunicación entre procesos remotos a través de la red, aplicando principios de arquitectura limpia y
separación estricta de responsabilidades mediante el concepto de **Contrato Compartido (Shared API)**. A diferencia de la iteración
anterior, la capa de datos ya no reside en memoria: el servidor delega las consultas a una base de datos **PostgreSQL** a través de
**JDBC**.

## Vista General del Proyecto

Este es un proyecto que huye del anti-patrón monolítico y demuestra cómo dividir físicamente un sistema en tres componentes lógicos: un
módulo `Shared API` que define el contrato neutro, un módulo `Server` que implementa la lógica y se conecta a la base de datos, y un
módulo `Client` que consume el servicio. Construido con **Java 21+** (probado en Java 26), y complementado con librerías como **Lombok**,
**SLF4J** y el **driver JDBC de PostgreSQL**. La comunicación entre cliente y servidor se logra de forma transparente a través de
invocaciones de métodos remotos y el uso de un archivo `.jar` como librería compartida.

---

### Módulo `Shared API` (El Contrato)

| Clase / Archivo     | Responsabilidad                                                                                                               |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `Person.java`       | Entidad de transferencia de datos. Implementada como *Java Record* para garantizar inmutabilidad total al viajar por la red.  |
| `RemoteServer.java` | Interfaz neutra. Define los métodos que el servidor expondrá y que el cliente podrá invocar remotamente.                      |
| `shared-api.jar`    | Archivo binario empaquetado resultante que contiene el contrato y es consumido como dependencia por el Servidor y el Cliente. |

### Módulo `Server`

| Clase / Archivo   | Responsabilidad                                                                                                                                             |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Repository.java` | Capa de acceso a datos. Gestiona la conexión JDBC a PostgreSQL y ejecuta las consultas parametrizadas contra la tabla `empleados`.                          |
| `Server.java`     | Implementa el contrato `RemoteServer`. Delega las búsquedas al `Repository` y formatea los resultados antes de devolverlos al cliente.                      |
| `Launcher.java`   | Clase principal que lee la configuración desde variables de entorno, levanta el `Repository`, registra el servidor y lo expone en el registro RMI (`1099`). |

### Módulo `Client`

| Clase / Archivo       | Responsabilidad                                                                                                                           |
|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| `RemoteClient.java`   | Se conecta al registro RMI, recupera el *Stub* (Proxy) aplicando *Cold Start Lookup* para guardar la referencia remota.                   |
| `ClientLauncher.java` | Clase de prueba que consume el contrato realizando peticiones remotas de forma transparente y muestra los datos obtenidos en la terminal. |

---

## Características Implementadas

- **Arquitectura de Contrato Compartido:** El proyecto está dividido estrictamente para evitar acoplamiento. Ni el cliente conoce la
  implementación del servidor, ni el servidor sabe quién lo consume; ambos solo dependen del `shared-api.jar`.
- **Inmutabilidad con Java Records:** Se abandonaron las clases tradicionales con múltiples *Getters/Setters* en favor de `record`,
  garantizando inmutabilidad segura en la red.
- **Persistencia Real con PostgreSQL vía JDBC:** La capa de datos se desplazó de un `Map` en memoria a una base de datos relacional.
  El `Repository` abre una conexión por consulta usando `DriverManager`, ejecuta sentencias `PreparedStatement` parametrizadas y
  mapea el `ResultSet` a un `Optional<Person>`, todo con gestión automática de recursos a través de *try-with-resources*.
- **Configuración Externalizada con Variables de Entorno:** Los parámetros de conexión (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`,
  `DB_PASS`) se leen desde el entorno en tiempo de ejecución. Los obligatorios fallan rápidamente con `IllegalArgumentException` si
  están ausentes; los opcionales cuentan con un valor por defecto (`5432` para el puerto).
- **Cold Start Lookup:** El cliente realiza la búsqueda en el registro (`Naming.lookup`) una única vez durante la instanciación. La
  referencia remota se almacena para evitar sobrecargar la red en peticiones consecutivas.
- **Observabilidad Integral:** Se eliminaron los métodos tradicionales como `System.out.println()` a favor de un logging estructurado (
  `@Slf4j`), lo cual es vital para depurar problemas de red, serialización o conectividad a la base de datos en entornos distribuidos.

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta / Librería                                                                                | Versión / Enlace                          |
|-------------------------------------------------------------------------------------------------------|-------------------------------------------|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 21+ (Recomendado 26)                      |
| [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/download/)                                       | Última versión estable (solo el `.jar`)   |
| [SLF4J API](https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.17/)                               | Librería de Logs (solo el `.jar`)         |
| [Logback Classic](https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.5.32/)              | Motor SLF4J (solo el `.jar`)              |
| [Logback Core](https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.5.32/)                    | Dependencia Logback (solo el `.jar`)      |
| [Project Lombok](https://projectlombok.org/download)                                                  | Última versión compatible                 |
| [PostgreSQL](https://www.postgresql.org/download/)                                                    | Instancia accesible con tabla `empleados` |

> **Nota:** Dado que este proyecto busca entender los fundamentos de la JVM, **no utiliza herramientas como Maven o Gradle**. Todo el
> proceso de compilación, empaquetado de dependencias inter-modulares (creación del `.jar`) y ejecución se realiza de manera manual mediante
> la interfaz de línea de comandos (CLI). El driver JDBC de PostgreSQL debe incluirse en el *classpath* del módulo `Server`.

### Pasos

1. **Compilar y Empaquetar el Contrato (Shared API)**
   Abre una terminal en la raíz del proyecto y ejecuta la compilación del paquete compartido para luego empaquetarlo como una librería
   `.jar`:
   ```bash
   # 1. Compilar los binarios
   javac Shared/*.java

   # 2. Empaquetar en un Library JAR
   jar cvf shared-api.jar Shared/*.class
   ```

2. **Compilar el Servidor y el Cliente**
   Debemos indicarle al compilador que utilice nuestro `shared-api.jar` y el driver JDBC en el *classpath* (`-cp`). *(Nota para Windows:
   Usa `;` en
   lugar de `:` para separar el classpath).*
   ```bash
   # Compilar el Servidor (incluye el driver JDBC de PostgreSQL)
   javac -cp "shared-api.jar:postgresql.jar:." Server/*.java

   # Compilar el Cliente
   javac -cp "shared-api.jar:." Client/*.java
   ```

3. **Configurar las Variables de Entorno**
   El servidor lee la configuración de la base de datos desde el entorno. Exporta las variables antes de lanzarlo:
   ```bash
   export DB_HOST=172.31.118.11
   export DB_PORT=5432       # Opcional: usa 5432 por defecto
   export DB_NAME=empresa
   export DB_USER=usuario
   export DB_PASS=user12345
   ```

4. **Ejecutar la Aplicación (Servidor)**
   Necesitarás abrir una consola dedicada para levantar el servidor y mantenerlo a la escucha. El driver JDBC debe estar en el *classpath*:
   ```bash
   java -cp "shared-api.jar:postgresql.jar:." Server.Launcher
   ```
   *Salida esperada:* `[INFO] RMI Remote Server is up and running on rmi://localhost:1099/RemoteServer`

5. **Ejecutar la Aplicación (Cliente)**
   En una **segunda consola**, lanza el cliente para realizar las pruebas de conexión e invocación remota:
   ```bash
   java -cp "shared-api.jar:." Client.ClientLauncher
   ```
   *Salida esperada:* El cliente se conectará al puerto `1099`, realizará las consultas definidas y mostrará los datos obtenidos desde
   la base de datos PostgreSQL.

---

## Conceptos Clave de Java RMI, JDBC y Arquitectura

| Concepto                  | Descripción                                                                                                                                                                                       |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Java RMI`                | Interfaz de programación (API) de Java que permite que un objeto en una Máquina Virtual invoque métodos de un objeto en otra de forma transparente.                                               |
| `Registro RMI (Registry)` | Servicio de directorio donde el servidor "publica" sus objetos remotos (puerto `1099` por defecto) y donde los clientes acuden para buscarlos.                                                    |
| `Stub (Proxy)`            | Objeto que actúa como representante local del objeto remoto en el lado del cliente, encargado de empaquetar y enrutar las peticiones por la red.                                                  |
| `JDBC`                    | API estándar de Java para conectarse a bases de datos relacionales. Usa un `Driver` específico del proveedor (en este caso PostgreSQL) para traducir las llamadas Java a protocolo de red nativo. |
| `PreparedStatement`       | Sentencia SQL parametrizada que previene inyección SQL al separar la estructura de la consulta de los datos enviados por el usuario.                                                              |
| `Shared Contract / API`   | Patrón donde se extraen las interfaces y modelos de datos a un módulo independiente (un JAR neutral) que tanto el cliente como el servidor deben usar.                                            |
| `Cold Start Lookup`       | Estrategia de inicialización donde los recursos costosos (como buscar en la red mediante `Naming.lookup`) se realizan una sola vez al inicio.                                                     |
| **Legacy Status (2026)**  | *Nota Enterprise:* RMI se considera *legacy* por sus problemas nativos de serialización y firewalls. En la actualidad se opta por **gRPC** o **REST**.                                            |