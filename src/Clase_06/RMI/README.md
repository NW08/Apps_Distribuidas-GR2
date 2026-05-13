# Sistema Distribuido Java RMI - Directorio de Empleados || `CLASE 06 (06|05|2026)`

Se hizo este proyecto como implementación académica de un sistema distribuido Cliente-Servidor utilizando **Java RMI (Remote Method
Invocation)**. Demuestra la comunicación entre procesos remotos a través de la red, aplicando principios de arquitectura limpia y
separación estricta de responsabilidades mediante el concepto de **Contrato Compartido (Shared API)**.

## Vista General del Proyecto

Este es un proyecto que huye del anti-patrón monolítico y demuestra cómo dividir físicamente un sistema en tres componentes lógicos: un
módulo `Shared API` que define el contrato neutro, un módulo `Server` que implementa la lógica y mantiene el estado, y un módulo `Client`
que consume el servicio. Construido con **Java 21+** (probado en Java 26), y complementado con librerías como **Lombok** y **SLF4J**. La
comunicación entre cliente y servidor se logra de forma transparente a través de invocaciones de métodos remotos y el uso de un archivo
`.jar` como librería compartida.

---

### Módulo `Shared API` (El Contrato)

| Clase / Archivo     | Responsabilidad                                                                                                               |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `Person.java`       | Entidad de transferencia de datos. Implementada como *Java Record* para garantizar inmutabilidad total al viajar por la red.  |
| `RemoteServer.java` | Interfaz neutra. Define los métodos que el servidor expondrá y que el cliente podrá invocar remotamente.                      |
| `shared-api.jar`    | Archivo binario empaquetado resultante que contiene el contrato y es consumido como dependencia por el Servidor y el Cliente. |

### Módulo `Server`

| Clase / Archivo         | Responsabilidad                                                                                                  |
|-------------------------|------------------------------------------------------------------------------------------------------------------|
| `RemoteServerImpl.java` | Implementa el contrato `RemoteServer`. Mantiene el estado del sistema mediante una base de datos en memoria.     |
| `ServerLauncher.java`   | Clase principal que levanta el servicio y lo expone a través del registro RMI (típicamente en el puerto `1099`). |

### Módulo `Client`

| Clase / Archivo       | Responsabilidad                                                                                                                 |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------|
| `RemoteClient.java`   | Se conecta al registro RMI, recupera el *Stub* (Proxy) aplicando *Cold Start Lookup* para guardar la referencia remota.         |
| `ClientLauncher.java` | Clase de prueba que consume el contrato realizando peticiones remotas de forma transparente y muestra los datos en la terminal. |

---

## Características Implementadas

- **Arquitectura de Contrato Compartido:** El proyecto está dividido estrictamente para evitar acoplamiento. Ni el cliente conoce la
  implementación del servidor, ni el servidor sabe quién lo consume; ambos solo dependen del `shared-api.jar`.
- **Inmutabilidad con Java Records:** Se abandonaron las clases tradicionales con múltiples *Getters/Setters* en favor de `record`,
  garantizando inmutabilidad segura en la red.
- **Rendimiento de Búsqueda O(1):** La estructura interna del servidor utiliza un `Map.of()` inmutable en lugar de iterar sobre un
  `ArrayList`, mejorando drásticamente el rendimiento de búsqueda.
- **Cold Start Lookup:** El cliente realiza la búsqueda en el registro (`Naming.lookup`) una única vez durante la instanciación. La
  referencia remota se almacena para evitar sobrecargar la red en peticiones consecutivas.
- **Observabilidad Integral:** Se eliminaron los métodos tradicionales como `System.out.println()` a favor de un logging estructurado (
  `@Slf4j`), lo cual es vital para depurar problemas de red o serialización en entornos distribuidos.

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta / Librería                                                                                | Versión / Enlace                     |
|-------------------------------------------------------------------------------------------------------|--------------------------------------|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 21+ (Recomendado 26)                 |
| [SLF4J API](https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.17/)                               | Librería de Logs (solo el `.jar`)    | 
| [Logback Classic](https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.5.32/)              | Motor SLF4J (solo el `.jar`)         | 
| [Logback Core](https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.5.32/)                    | Dependencia Logback (solo el `.jar`) | 
| [Project Lombok](https://projectlombok.org/download)                                                  | Última versión compatible            |

> **Nota:** Dado que este proyecto busca entender los fundamentos de la JVM, **no utiliza herramientas como Maven o Gradle**. Todo el
> proceso de compilación, empaquetado de dependencias inter-modulares (creación del `.jar`) y ejecución se realiza de manera manual mediante
> la interfaz de línea de comandos (CLI).

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
   Debemos indicarle al compilador que utilice nuestro nuevo `shared-api.jar` en el *classpath* (`-cp`). *(Nota para Windows: Usa `;` en
   lugar de `:` para separar el classpath).*
   ```bash
   # Compilar el Servidor
   javac -cp "shared-api.jar:." Server/*.java

   # Compilar el Cliente
   javac -cp "shared-api.jar:." Client/*.java
   ```

3. **Ejecutar la Aplicación (Servidor)**
   Necesitarás abrir una consola dedicada para levantar el servidor y mantenerlo a la escucha:
   ```bash
   java -cp "shared-api.jar:." Server.ServerLauncher
   ```
   *Salida esperada:* `[INFO] RMI Remote Server is up and running on rmi://localhost:1099/RemoteServer`

4. **Ejecutar la Aplicación (Cliente)**
   En una **segunda consola**, lanza el cliente para realizar las pruebas de conexión e invocación remota:
   ```bash
   java -cp "shared-api.jar:." Client.ClientLauncher
   ```
   *Salida esperada:* El cliente se conectará al puerto `1099`, realizará las consultas definidas y mostrará los datos obtenidos desde la
   memoria del servidor.

---

## Conceptos Clave de Java RMI y Arquitectura

| Concepto                  | Descripción                                                                                                                                            |
|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Java RMI`                | Interfaz de programación (API) de Java que permite que un objeto en una Máquina Virtual invoque métodos de un objeto en otra de forma transparente.    |
| `Registro RMI (Registry)` | Servicio de directorio donde el servidor "publica" sus objetos remotos (puerto `1099` por defecto) y donde los clientes acuden para buscarlos.         |
| `Stub (Proxy)`            | Objeto que actúa como representante local del objeto remoto en el lado del cliente, encargado de empaquetar y enrutar las peticiones por la red.       |
| `Shared Contract / API`   | Patrón donde se extraen las interfaces y modelos de datos a un módulo independiente (un JAR neutral) que tanto el cliente como el servidor deben usar. |
| `Cold Start Lookup`       | Estrategia de inicialización donde los recursos costosos (como buscar en la red mediante `Naming.lookup`) se realizan una sola vez al inicio.          |
| **Legacy Status (2026)**  | *Nota Enterprise:* RMI se considera *legacy* por sus problemas nativos de serialización y firewalls. En la actualidad se opta por **gRPC** o **REST**. |